package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.UserRecipe;

import java.util.List;

public interface UserRecipeService {

    UserRecipe uploadUserRecipe(UserRecipe userRecipe, int userId);

    List<UserRecipe> getUserRecipes(int userId, int page, int size);

    // Get a single user recipe by ID and User ID to ensure the user has access
    UserRecipe getUserRecipeByIdAndUser(int recipeId, int userId);

    // Delete a user's recipe
    void deleteUserRecipe(int recipeId, int userId);
}
