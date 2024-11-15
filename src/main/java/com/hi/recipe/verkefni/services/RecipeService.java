package com.hi.recipe.verkefni.services;


import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;

import java.time.LocalDateTime;
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

    List<Recipe> findByCategoriesIn(Set<Category> categories);

    Optional<Recipe> findRecipeById(int id);

    List<Recipe> findByTitleAndCategories(String query, Set<Category> categories);

    List<Recipe> findByTagsInAndCategoriesIn(Set<RecipeTag> tags, Set<Category> categories);

    List<Recipe> findByTitleAndTagsAndCategories(String query, Set<RecipeTag> tags, Set<Category> categories);

    List<Recipe> findByTitleContainingIgnoreCase(String keyword);

    List<Recipe> findByTitleAndTags(String title, Set<RecipeTag> tags);

    List<Recipe> findByDate();

    String formatDate(LocalDateTime date);

    void addTempRatingToRecipe(int recipeId, int score);

    // Paginated recipes sorted by average rating
    List<Recipe> findAllByAverageRatingDesc();

    // Fetch distinct category names directly from the enum
    Set<String> getDistinctCategoryNames();

    List<Recipe> filterRecipes(String query, Set<RecipeTag> tags);

    List<Recipe> getSortedRecipes(String sort, Set<Category> categories);

    // Method to convert a set of category strings to Set<Category> enum
    Set<Category> convertToCategoryEnum(Set<String> categoryStrings);

    Set<RecipeTag> convertToRecipeTagEnum(Set<String> tagStrings);
}


