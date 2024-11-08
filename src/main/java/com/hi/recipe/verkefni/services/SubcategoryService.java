package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.Subcategory;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SubcategoryService {

    Optional<Subcategory> getSubcategoryByName(String name);

    List<Recipe> getRecipesBySubcategoryId(Long subcategoryId);

    // Get Subcategories by Category ID
    List<Subcategory> getSubcategoriesByCategoryId(Long categoryId);

    List<Recipe> getRecipesBySubcategoryName(String subcategoryName);

    // Get Subcategories by Category name
    List<Subcategory> getSubcategoriesByCategoryName(String categoryName);

    Optional<Subcategory> getSubcategoryByNameAndCategoryId(String subcategoryName, Long categoryId);


    Optional<Category> getCategoryBySubcategoryName(String subcategoryName);

    boolean checkSubcategoryExistence(String subcategoryName);

    Optional<Subcategory> findSubcategoryByNameAndCategory(String subcategoryName, Set<Category> categories);

    Subcategory save(Subcategory subcategory);

    // Create a new subcategory if it doesn't exist
    Subcategory createSubcategoryIfNotExists(String subcategoryName, Category category);

    Subcategory createSubcategory(Subcategory subcategory);

    // Update an existing Subcategory
    Subcategory updateSubcategory(Long id, Subcategory subcategory);

    // Service method to get a Subcategory by ID
    Optional<Subcategory> findBySubcategoryId(Long subcategoryId);


    boolean deleteSubcategory(Long id);

    // Method to check if a subcategory exists and return its category
    Optional<Category> checkSubcategoryExistenceAndGetCategory(String subcategoryName);

    // Search Subcategories by Name Pattern
    List<Subcategory> searchSubcategoriesByName(String namePattern);

    boolean doesSubcategoryExistInCategory(String subcategoryName, String categoryName);

    boolean doesSubcategoryExist(String subcategoryName);
}
