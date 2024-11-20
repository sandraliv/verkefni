package com.hi.recipe.verkefni.services;


import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import com.hi.recipe.verkefni.klasar.User;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RecipeService {

    List<Recipe> findAll();

    List<Recipe> findAllPaginated();

    List<Recipe> findAllPaginated(int page, int size);

    Recipe save(Recipe recipe);

    Optional<Recipe> findById(int id);

    void delete(Recipe recipe);

    void deleteById(int id);

    List<Recipe> findByTagsIn(Set<RecipeTag> tags, int page, int size);

    List<Recipe> findByTitleAndCategories(String title, Set<Category> categories);

    List<Recipe> findByCategoriesIn(Set<Category> categories, int page, int size);

    Optional<Recipe> findRecipeById(int id);

    List<Recipe> findByTitleContainingIgnoreCase(String keyword);

    List<Recipe> findByTitleAndTags(String title, Set<RecipeTag> tags);

    List<Recipe> findByDate(int page, int size);

    String formatDate(LocalDateTime date);

    // Paginated recipes sorted by average rating
    List<Recipe> findAllByAverageRatingDesc(int page, int size);

    @Transactional
    void addRating(int recipeId, User user, int score);

    List<Recipe> getSortedRecipes(String sort, Set<Category> categories, int page, int size);

    // Method to convert a set of category strings to Set<Category> enum
    Set<Category> convertToCategoryEnum(Set<String> categoryStrings);

    // Method to remove rating from a recipe
    void removeRatingFromRecipe(int recipeId);

    int getTotalPages(int staerd);
}


