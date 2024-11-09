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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final RecipeService recipeService;
    private final SubcategoryService subcategoryService;

    // Constructor injection
    @Autowired
    public CategoryController(CategoryService categoryService,RecipeService recipeService,SubcategoryService subcategoryService) {
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;
        this.recipeService = recipeService;
    }
    //================================================================================
    // GET Methods
    //================================================================================


    @GetMapping("/allCategories")
    public ResponseEntity<Set<Category>> getAllCategories() {
        Set<Category> categories = categoryService.getAllCategories();

        // Log for debugging
        System.out.println("Fetched categories: " + categories);

        if (categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // No categories found
        } else {
            return new ResponseEntity<>(categories, HttpStatus.OK); // Categories found
        }
    }

    // Get category by name
    @GetMapping("/byName/{name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String categoryName) {
        Optional<Category> categoryOpt = categoryService.getCategoryByName(categoryName);
        return categoryOpt.map(ResponseEntity::ok)   // If the category exists, return 200 OK with the category
                .orElseGet(() -> ResponseEntity.notFound().build());  // If not found, return 404 Not Found
    }


    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        // Fetch category by ID from the service
        Optional<Category> categoryOpt = categoryService.getCategoryById(id);

        // Check if category is present
        if (categoryOpt.isPresent()) {
            Category category = categoryOpt.get();

            // Fetch subcategories and recipes (if not already set in the entity model)
            // You can assume that Category entity has relationships with Subcategory and Recipe entities.
            category.getSubcategories();  // Assuming it's a lazy-loaded relationship
            category.getRecipes();  // Assuming it's a lazy-loaded relationship

            // Return the category with subcategories and recipes
            return new ResponseEntity<>(category, HttpStatus.OK);
        } else {
            // Return NOT FOUND if category doesn't exist
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    //================================================================================
    // POST Methods
    //================================================================================

    // Method to create a category and/or subcategory without connecting it to any recipe

    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(
            @RequestParam("categoryName") String categoryName,
            @RequestParam(value = "subcategoryName", required = false) String subcategoryName) {

        // Normalize category name using utility method
        String normalizedCategoryName = CategoryUtils.normalizeCategoryName(categoryName);

        // Check if the category already exists using the normalized name
        Optional<Category> existingCategory = categoryService.getCategoryByName(normalizedCategoryName);
        Category category;

        if (existingCategory.isPresent()) {
            // If category exists, return a bad request (already exists)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else {
            // If category doesn't exist, create a new one
            category = new Category();
            category.setName(normalizedCategoryName);
            category = categoryService.createCategory(category);
        }

        // Optionally, create a subcategory if subcategoryName is provided
        if (subcategoryName != null) {
            // Normalize subcategory name using utility method
            String normalizedSubcategoryName = CategoryUtils.normalizeCategoryName(subcategoryName);

            // Check if the subcategory already exists for the category
            Optional<Subcategory> existingSubcategory = subcategoryService.getSubcategoryByNameAndCategoryId(normalizedSubcategoryName, category.getId());

            if (!existingSubcategory.isPresent()) {
                // Create new subcategory if it doesn't exist
                Subcategory subcategory = new Subcategory();
                subcategory.setName(normalizedSubcategoryName);
                subcategory.setCategory(category);  // Associate subcategory with category
                subcategoryService.createSubcategory(subcategory);
            }
        }

        // Return the created category with status 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    //================================================================================
    // PATCH Methods
    //================================================================================


    @PatchMapping("/addRecipesToCategoryAndSubcategory")
    public ResponseEntity<String> addRecipesToCategoryAndSubcategory(
            @RequestParam String recipeIds,  // Comma-separated recipe IDs
            @RequestParam(required = false) Long categoryId,  // Optional category ID
            @RequestParam(required = false) String categoryName,  // Optional category name
            @RequestParam(required = false) Long subcategoryId,  // Optional subcategory ID
            @RequestParam(required = false) String subcategoryName  // Optional subcategory name
    ) {
        // Initialize category and subcategory as optional
        Optional<Category> category = Optional.empty();
        Optional<Subcategory> subcategory = Optional.empty();

        // Fetch the category by ID or name
        if (categoryId != null) {
            category = categoryService.getCategoryById(categoryId);
            if (category.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found.");
            }
        } else if (categoryName != null) {
            category = (categoryService.getCategoryByName(categoryName));
            if (category.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found.");
            }
        }

        // Fetch the subcategory by ID or name
        if (subcategoryId != null) {
            subcategory = subcategoryService.findBySubcategoryId(subcategoryId);
            if (subcategory.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subcategory not found.");
            }
        } else if (subcategoryName != null) {
            subcategory = (subcategoryService.getSubcategoryByName(subcategoryName));
            if (subcategory.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subcategory not found.");
            }
        }

        // Convert the comma-separated recipeIds into a list of Longs
        List<Long> recipeIdList = Arrays.stream(recipeIds.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // Add recipes to the category and subcategory
        for (Long recipeId : recipeIdList) {
            Optional<Recipe> recipeOpt = recipeService.findById(Math.toIntExact(recipeId));
            if (recipeOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found with ID " + recipeId);
            }

            Recipe recipe = recipeOpt.get();

            // Add category to recipe if provided
            category.ifPresent(cat -> recipe.getCategories().add(cat));

            // Add subcategory to recipe if provided
            subcategory.ifPresent(sub -> recipe.getSubcategories().add(sub));

            // Save the updated recipe
            recipeService.save(recipe);
        }

        return ResponseEntity.ok("Recipes successfully added to the category and/or subcategory.");
    }



    //================================================================================
    // PUT Methods
    //================================================================================

    // Update an existing category
    @PutMapping("/update/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {

        Category updatedCategory = categoryService.updateCategory(id, category);
        if (updatedCategory != null) {
            return ResponseEntity.ok(updatedCategory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //================================================================================
    // DELETE Methods
    //================================================================================

    // Delete a category
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        boolean deleted = categoryService.deleteCategory(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
