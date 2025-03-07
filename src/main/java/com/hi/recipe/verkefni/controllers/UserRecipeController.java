package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.UserRecipe;
import com.hi.recipe.verkefni.services.UserRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-recipes")
public class UserRecipeController {

    @Autowired
    private UserRecipeService userRecipeService;


    @PostMapping("/{userId}/upload")
    public ResponseEntity<UserRecipe> uploadRecipe(@RequestBody UserRecipe userRecipe, @PathVariable int userId) {
        // Log the received data before saving
        System.out.println("Uploading Recipe for User with ID: " + userId);
        System.out.println("Title: " + userRecipe.getTitle());
        System.out.println("Description: " + userRecipe.getDescription());
        System.out.println("Ingredients: " + userRecipe.getIngredients());
        System.out.println("Instructions: " + userRecipe.getInstructions());

        // Associate the recipe with the user by userId and save the UserRecipe
        UserRecipe uploadedUserRecipe = userRecipeService.uploadUserRecipe(userRecipe, userId);

        // Log the details of the uploaded UserRecipe, including the generated userRecipeId
        System.out.println("Uploaded UserRecipe with ID: " + uploadedUserRecipe.getId());
        System.out.println("Title: " + uploadedUserRecipe.getTitle());
        System.out.println("Description: " + uploadedUserRecipe.getDescription());
        System.out.println("Ingredients: " + uploadedUserRecipe.getIngredients());
        System.out.println("Instructions: " + uploadedUserRecipe.getInstructions());

        // Return the response with the uploaded UserRecipe
        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedUserRecipe);
    }


    @GetMapping("/{userId}/getUserRecipes")
    public ResponseEntity<List<UserRecipe>> getUserRecipes(@PathVariable int userId,  // Use PathVariable instead of RequestParam
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        // Get the list of recipes from the service
        List<UserRecipe> userRecipes = userRecipeService.getUserRecipes(userId, page, size);

        // Check if there are any recipes
        if (userRecipes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Return 404 if no recipes found
        }
        return ResponseEntity.ok(userRecipes);  // Return 200 OK with the list of recipes
    }

    // Get a specific recipe uploaded by a user, ensuring userId matches
    @GetMapping("/{id}")
    public ResponseEntity<UserRecipe> getUserRecipeByIdAndUserId(@PathVariable int id,
                                                                 @RequestParam int userId) {
        try {
            UserRecipe userRecipe = userRecipeService.getUserRecipeByIdAndUser(id, userId);
            if (userRecipe != null) {
                return ResponseEntity.ok(userRecipe);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Delete a recipe uploaded by the user
    @DeleteMapping("/{user-recipeId}")
    public void deleteRecipe(@PathVariable int recipeId, @RequestParam int userId) {
        userRecipeService.deleteUserRecipe(recipeId, userId);
    }
}
