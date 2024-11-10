package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.Subcategory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CategoryService {
    // Expecting a String (category name)
    Optional<Category> getCategoryByName(String categoryName);

    // Create a new category if it doesn't exist
    Category createCategoryIfNotExists(String categoryName);

    // Create a new category
    Category createCategory(Category category);

    // Get Category associated with a Recipe by Recipe ID
    List<Category> getCategoriesByRecipeId(Long recipeId);

    // Find all Subcategories by Category ID
    List<Subcategory> getSubcategoriesByCategoryId(Long categoryId);

    Category saveCategory(Category category);

    // Update an existing category (name)
    Category updateCategory(Long id, Category category);

    boolean deleteCategory(Long id);

    Optional<Category> getCategoryById(Long categoryId) ;


    // Search Category by Name Pattern
    List<Category> searchCategoriesByName(String namePattern);


    // Helper method to get the names of subcategories for a recipe
    Set<String> getSubcategoryNamesForRecipe(Recipe recipe);


    // Helper method to get the first subcategory name associated with the recipe (you can adjust this logic if needed)
    String getSubcategoryName(Recipe recipe);

    // Method to fetch all categories
    Set<Category> getAllCategories();

    // Find categories by their names
    Set<Category> findCategoriesByNames(Set<String> categoryNames);

    // Helper method to convert Set<Category> to Set<String> (extract category names)
    Set<String> extractCategoryNames(Set<Category> categories);
}
