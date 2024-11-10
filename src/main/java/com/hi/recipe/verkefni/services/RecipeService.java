package com.hi.recipe.verkefni.services;


import com.hi.recipe.verkefni.klasar.Rating;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RecipeService {
    List<Recipe> findAll();

    List<Recipe> findAllPaginated();

    Recipe save(Recipe recipe);

    Optional<Recipe> findById(int id);

    void delete(Recipe recipe);

    void deleteById(int id);

    List<Recipe> findByTagsIn(Set<RecipeTag> tags);

    List<Recipe> findByTitleContainingIgnoreCase(String keyword);

    List<Recipe> findByTitleAndTags(String title, Set<RecipeTag> tags);

    List<Recipe> findByDate();

    // Helper method to convert tags to list

    List<String> convertTagsToList(Recipe recipe);

    List<String> convertCategoriesToList(Recipe recipe);

    List<String> convertSubcategoriesToList(Recipe recipe);

    <T> List<String> convertEntitiesToList(Collection<T> entities);

    // Helper method to format date
    String formatDate(LocalDateTime date);

    void addTempRatingToRecipe(int recipeId, int score);

    List<Recipe> findAllByAverageRatingDesc();

    List<Rating> findByRecipeId(int recipeId);

    List<Recipe> findByCategory(String categoryName);

    List<Recipe> findBySubcategory(String subcategoryName);

    List<Recipe> findByCategoriesId(Long categoryId);

    List<Recipe> findBySubcategoryId(Long subcategoryId);

    // Find recipes by category and subcategory using IDs
    List<Recipe> findByCategoryAndSubcategory(Long categoryId, Long subcategoryId);

    // Find recipes by category name and subcategory name
    List<Recipe> findByCategoryNameIncludingSubcategories(String categoryName, String subcategoryName);



}
