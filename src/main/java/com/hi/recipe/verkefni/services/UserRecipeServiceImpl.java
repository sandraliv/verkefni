package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.klasar.UserRecipe;
import com.hi.recipe.verkefni.repository.UserRecipeRepository;
import com.hi.recipe.verkefni.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRecipeServiceImpl implements UserRecipeService {

    @Autowired
    private UserRecipeRepository userRecipeRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public UserRecipe uploadUserRecipe(UserRecipe userRecipe, int userId) {
        // Fetch the user from the database by userId
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        // Set the user to the UserRecipe
        userRecipe.setUser(user);
        // Save the recipe to the repository
        return userRecipeRepository.save(userRecipe);
    }

    @Override
    public List<UserRecipe> getUserRecipes(int userId, int page, int size) {
        // Ensure the page and size are non-negative
        if (page < 0) page = 0;
        if (size <= 0) size = 10;  // Default to 10 if size is less than or equal to 0

        // Create Pageable object for pagination
        Pageable pageable = PageRequest.of(page, size);

        // Use the repository method with pagination
        Page<UserRecipe> userRecipesPage = userRecipeRepository.findByUserId(userId, pageable);

        // Return the content (list of user recipes)
        return userRecipesPage.getContent();
    }

    // Get a single user recipe by ID and User ID to ensure the user has access
    @Override
    public UserRecipe getUserRecipeByIdAndUser(int recipeId, int userId) {
        return userRecipeRepository.findByIdAndUserId(recipeId, userId);
    }

    // Delete a user's recipe
    @Override
    public void deleteUserRecipe(int recipeId, int userId) {
        UserRecipe userRecipe = userRecipeRepository.findByIdAndUserId(recipeId, userId);
        if (userRecipe != null) {
            userRecipeRepository.delete(userRecipe);
        }
    }
}
