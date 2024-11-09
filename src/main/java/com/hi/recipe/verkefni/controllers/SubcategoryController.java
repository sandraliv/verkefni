package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.Subcategory;
import com.hi.recipe.verkefni.services.CategoryService;
import com.hi.recipe.verkefni.services.RecipeService;
import com.hi.recipe.verkefni.services.SubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.hi.recipe.verkefni.utils.CategoryUtils.normalizeCategoryName;

@RestController
@RequestMapping("/subcategories")
public class SubcategoryController {
    private final CategoryService categoryService;
    private final RecipeService recipeService;
    private final SubcategoryService subcategoryService;

    @Autowired
    public SubcategoryController(CategoryService categoryService, RecipeService recipeService, SubcategoryService subcategoryService) {
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;
        this.recipeService = recipeService;
    }

    //================================================================================
    // GET Methods
    //================================================================================

    // Endpoint to get recipes by subcategory ID or name
    @GetMapping("/subcategory")
    public ResponseEntity<List<Recipe>> getRecipesBySubcategory(
            @RequestParam(value = "subcategoryId", required = false) Long subcategoryId,
            @RequestParam(value = "subcategoryName", required = false) String subcategoryName) {

        List<Recipe> recipes;

        if (subcategoryId != null) {
            // Fetch recipes by subcategory ID
            recipes  = subcategoryService.getRecipesBySubcategoryId(subcategoryId);
        } else if (subcategoryName != null) {
            // Fetch recipes by subcategory Name
            recipes = subcategoryService.getRecipesBySubcategoryName(subcategoryName);
        } else {
            return ResponseEntity.badRequest().build(); // Bad Request if neither parameter is provided
        }

        return ResponseEntity.ok(recipes);
    }

    // Get subcategory by its name
    @GetMapping("/byName/{name}")
    public ResponseEntity<Subcategory> getSubcategoryByName(@PathVariable("name") String name) {
        Optional<Subcategory> subcategory = subcategoryService.getSubcategoryByName(name);
        if (subcategory.isPresent()) {
            return ResponseEntity.ok(subcategory.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getSubByCategory")
    public ResponseEntity<List<Subcategory>> getSubcategoriesByCategory(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "categoryName", required = false) String categoryName) {

        List<Subcategory> subcategories;

        // If categoryId is provided, use it to fetch subcategories
        if (categoryId != null) {
            subcategories = subcategoryService.getSubcategoriesByCategoryId(categoryId);
        } else if (categoryName != null) {
            // Normalize category name first (e.g., "baking" -> "Baking")
            String normalizedCategoryName = normalizeCategoryName(categoryName);

            // First, try querying by normalized category name
            subcategories = subcategoryService.getSubcategoriesByCategoryName(normalizedCategoryName);

            // If no results are found, try searching for the original, unnormalized category name
            if (subcategories.isEmpty()) {
                subcategories = subcategoryService.getSubcategoriesByCategoryName(categoryName);
            }
        } else {
            return ResponseEntity.badRequest().body(null);  // Return error if neither categoryId nor categoryName is provided
        }

        return ResponseEntity.ok(subcategories);
    }

    /**
     * Endpoint to check if a subcategory exists in a specific category by category name
     */
    @GetMapping("/check")
    public ResponseEntity<String> checkSubcategoryExistence(
            @RequestParam("subcategoryName") String subcategoryName,
            @RequestParam("categoryName") String categoryName) {

        // Normalize both category and subcategory names using the utility method
        String normalizedCategoryName = normalizeCategoryName(categoryName);
        String normalizedSubcategoryName = normalizeCategoryName(subcategoryName);

        boolean existsInCategory = subcategoryService.doesSubcategoryExistInCategory(normalizedSubcategoryName, normalizedCategoryName);

        if (existsInCategory) {
            return ResponseEntity.ok("Subcategory exists in this category.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subcategory does not exist in this category.");
        }
    }

    /**
     * @param subcategoryName The name of the subcategory to check.
     * @return A ResponseEntity with the Category if it exists, or a 404 if not found.
     */
    @GetMapping("/existsSub")
    public ResponseEntity<Void> checkSubcategoryExistence(@RequestParam("subcategoryName") String subcategoryName) {
        boolean exists = subcategoryService.checkSubcategoryExistence(subcategoryName);

        if (exists) {
            return ResponseEntity.ok().build();  // Return 200 OK if the subcategory exists
        } else {
            return ResponseEntity.notFound().build();  // Return 404 Not Found if it doesn't exist
        }
    }


    //================================================================================
    // POST Methods
    //================================================================================

    @PostMapping("/createSubcategory")
    public ResponseEntity<Subcategory> createSubcategory(
            @RequestParam("subcategoryName") String subcategoryName,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "categoryName", required = false) String categoryName) {

        Category category = null;

        // Normalize subcategory name
        String normalizedSubcategoryName = normalizeCategoryName(subcategoryName);


        // Normalize category name if provided
        if (categoryName != null) {
            categoryName = normalizeCategoryName(categoryName);
            System.out.println("Normalized category name: " + categoryName);
        }

        // 1. First, check if we have a categoryId
        if (categoryId != null) {
            Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
            if (!categoryOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Category not found
            }
            category = categoryOpt.get();
            System.out.println("Category found by ID: " + category.getName());
        }
        // 2. If categoryId is not provided, check for categoryName
        else if (categoryName != null) {
            Optional<Category> categoryByName = categoryService.getCategoryByName(categoryName);
            if (categoryByName.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Category not found by name
            }

            category = categoryByName.get();

            System.out.println("Category found by Name: " + category.getName());
        } else {
            // If neither categoryId nor categoryName is provided, return a bad request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Invalid request
        }

        // Check if the subcategory already exists
        Optional<Subcategory> existingSubcategory = subcategoryService.getSubcategoryByNameAndCategoryId(normalizedSubcategoryName, category.getId());
        if (existingSubcategory.isPresent()) {
            System.out.println("Subcategory already exists: " + existingSubcategory.get().getName());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Subcategory already exists
        } else {
            // Create the new subcategory since it doesn't exist
            Subcategory subcategory = new Subcategory();
            subcategory.setName(normalizedSubcategoryName);
            subcategory.setCategory(category);
            Subcategory createdSubcategory = subcategoryService.createSubcategory(subcategory);

            System.out.println("Subcategory created successfully: " + createdSubcategory.getName()+ createdSubcategory.getCategory());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSubcategory);
        }
    }

    //================================================================================
    // DELETE Methods
    //================================================================================

    // Delete a category
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSubcategory(@PathVariable Long id) {
        boolean deleted = subcategoryService.deleteSubcategory(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}







