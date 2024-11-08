package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.Subcategory;
import com.hi.recipe.verkefni.services.CategoryService;
import com.hi.recipe.verkefni.services.RecipeService;
import com.hi.recipe.verkefni.services.SubcategoryService;
import com.hi.recipe.verkefni.utils.CategoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    // Get all subcategories that are in a category
    @GetMapping("/getSubByCategory")
    public ResponseEntity<List<Subcategory>> getSubcategoriesByCategory(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "categoryName", required = false) String categoryName) {

        List<Subcategory> subcategories;

        if (categoryId != null) {
            // If categoryId is provided, use it to get the subcategories
            subcategories = subcategoryService.getSubcategoriesByCategoryId(categoryId);
        } else if (categoryName != null) {
            // If categoryName is provided, use it to get the subcategories
            subcategories = subcategoryService.getSubcategoriesByCategoryName(categoryName);
        } else {
            return ResponseEntity.badRequest().body(null);  // Return error if neither is provided
        }

        return ResponseEntity.ok(subcategories);
    }

    //================================================================================
    // POST Methods
    //================================================================================


    // Create a new subcategory associated with a category
    /*
    @PostMapping("/create")
    public ResponseEntity<Subcategory> createSubcategory(
            @RequestParam("subcategoryName") String subcategoryName,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "categoryName", required = false) String categoryName) {

        Category category = null;

        // First, check if we have a categoryId
        if (categoryId != null) {
            Optional<Category> categoryOpt = categoryService.getCategoryById(categoryId);
            if (!categoryOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Category not found
            }
            category = categoryOpt.get();
        }
        // If categoryId is not provided, check for categoryName
        else if (categoryName != null) {
            Optional<Category> categoryByName = categoryService.getCategoryByName(categoryName);
            if (categoryByName.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Category not found by name
            }
            category = categoryByName.get();
            System.out.println("got category by name");
        } else {
            // If neither categoryId nor categoryName is provided, return a bad request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Invalid request
        }

        // Check if the subcategory already exists under this category
        Optional<Subcategory> existingSubcategory = null;
        if (category != null) {
            // First check by category ID (this is the most efficient method if we already have the category)
            existingSubcategory = subcategoryService.getSubcategoryByNameAndCategoryId(subcategoryName, category.getId());

            // If not found by ID, check by category name (just in case)
            if (existingSubcategory == null) {
                List<Subcategory> subcategories = subcategoryService.getSubcategoriesByCategoryName(category.getName());
                // Look for the subcategory by name within the list of subcategories
                existingSubcategory = Optional.ofNullable(subcategories.stream()
                        .filter(sub -> sub.getName().equals(subcategoryName))
                        .findFirst()
                        .orElse(null));
            }
        }

        // Create the subcategory and associate it with the category
        Subcategory subcategory = new Subcategory();
        subcategory.setName(subcategoryName);
        subcategory.setCategory(category); // Associate with category
        Subcategory createdSubcategory = subcategoryService.createSubcategory(subcategory);


        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubcategory); // Return created subcategory
    }*/
    public ResponseEntity<Subcategory> createSubcategory(
            @RequestParam("subcategoryName") String subcategoryName,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "categoryName", required = false) String categoryName) {

        Category category = null;

        // Log incoming request params
        System.out.println("Creating subcategory: " + subcategoryName + ", CategoryId: " + categoryId + ", CategoryName: " + categoryName);

        // Normalize subcategory name
        String normalizedSubcategoryName = CategoryUtils.normalizeCategoryName(subcategoryName);
        System.out.println("Normalized subcategory name: " + normalizedSubcategoryName);

        // Normalize category name if provided
        if (categoryName != null) {
            categoryName = CategoryUtils.normalizeCategoryName(categoryName);
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

            System.out.println("Subcategory created successfully: " + createdSubcategory.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSubcategory);
        }
    }
    /**
     * Endpoint to check if a subcategory exists in a specific category by category name
     */
    @GetMapping("/check")
    public ResponseEntity<String> checkSubcategoryExistence(
            @RequestParam("subcategoryName") String subcategoryName,
            @RequestParam("categoryName") String categoryName) {

        // Normalize both category and subcategory names using the utility method
        String normalizedCategoryName = CategoryUtils.normalizeCategoryName(categoryName);
        String normalizedSubcategoryName = CategoryUtils.normalizeCategoryName(subcategoryName);

        boolean existsInCategory = subcategoryService.doesSubcategoryExistInCategory(normalizedSubcategoryName, normalizedCategoryName);

        if (existsInCategory) {
            return ResponseEntity.ok("Subcategory exists in this category.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subcategory does not exist in this category.");
        }
    }


    /**
     * Endpoint to check if a subcategory exists anywhere in the database
     */
    /*@GetMapping("/checkAll")
    public ResponseEntity<String> checkSubcategoryExistenceInAllCategories(
            @RequestParam("subcategoryName") String subcategoryName) {

       /* boolean exists = subcategoryService.doesSubcategoryExist(subcategoryName);

        if (exists) {
            return ResponseEntity.ok("Subcategory exists.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subcategory does not exist.");
        }


    }*/

    /**
     * Endpoint to check if a subcategory exists and get its parent category.
     *
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
}







