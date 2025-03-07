package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.UserRecipe;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import com.hi.recipe.verkefni.repository.UserRecipeRepository;
import com.hi.recipe.verkefni.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/recipes")
public class ImageController {

    private final ImageService imageService;
    private final RecipeRepository recipeRepository;
    private final UserRecipeRepository userRecipeRepository;

    @Autowired
    public ImageController(ImageService imageService, RecipeRepository recipeRepository, UserRecipeRepository userRecipeRepository) {
        this.imageService = imageService;
        this.recipeRepository = recipeRepository;
        this.userRecipeRepository = userRecipeRepository;

    }

    /**
     * @param recipeId Id á uppskrift sem á að breyta
     * @param file Myndin sem á að setja á uppskrift jpeg/jpg
     * @return skilaboð að allt hafi gengið eftir eða villu
     */
    @PostMapping("/{recipeId}/upload")
    public ResponseEntity<String> uploadImageToRecipe(@PathVariable int recipeId, @RequestParam("file") MultipartFile file) {
        try {
            // Finna uppskrift með id
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

            // Hlaða upp mynd inná cloudinary og fá urlið til baka
            String imageUrl = imageService.uploadImage(file);

            // Geyma url með uppskrift
            recipe.setImage_url(imageUrl);

            // Savea í gagnagrunni
            recipeRepository.save(recipe);
            return ResponseEntity.ok("Image uploaded and added to recipe successfully. Image URL: " + imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/{userId}/{user_recipe_id}/upload")
    public ResponseEntity<String> uploadImageToUserRecipe(@PathVariable int user_recipe_id,
                                                          @RequestParam("file") MultipartFile file,
                                                          @PathVariable int userId) {
        try {
            // Find the user recipe by both user recipe ID and user ID to ensure the user is authorized
            UserRecipe userRecipe = userRecipeRepository.findByIdAndUserId(user_recipe_id, userId);

            // If no recipe is found, this will throw an exception and return 404
            if (userRecipe == null) {
                return ResponseEntity.status(404).body("Recipe not found or not owned by this user.");
            }

            // Upload the image to Cloudinary (or other service) and get the URL
            String imageUrl = imageService.uploadImage(file);

            // Set the image URL to the recipe
            userRecipe.setImage_url(imageUrl);

            // Save the updated recipe back to the database
            userRecipeRepository.save(userRecipe);

            // Return a success message with the image URL
            return ResponseEntity.ok("Image uploaded and added to recipe successfully. Image URL: " + imageUrl);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }


    /**
     * @param recipeId Id á uppskrift sem á að breyta
     * @param file Myndin sem á að setja á uppskrift jpeg/jpg
     * @return skilaboð að allt hafi gengið eftir eða villu
     */
    @PatchMapping("/{recipeId}/replaceImg")
    public ResponseEntity<String> replaceRecipeImage(@PathVariable int recipeId, @RequestParam("file") MultipartFile file) {
        try {
            Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
            String imageUrl = imageService.uploadImage(file);
            recipe.setImage_url(imageUrl);
            recipeRepository.save(recipe);
            return ResponseEntity.ok("Image changed and added to recipe successfully. Image URL: " + imageUrl);
        } catch (IOException e) {
            return  ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PatchMapping("/{userId}/{user_recipe_id}/replaceImg")
    public ResponseEntity<String> replaceUserRecipeImage(@PathVariable int user_recipe_id,
                                                         @RequestParam("file") MultipartFile file,
                                                         @PathVariable int userId) {
        try {
            UserRecipe userRecipe = userRecipeRepository.findByIdAndUserId(user_recipe_id, userId);
            if (userRecipe == null) {
                return ResponseEntity.status(404).body("Recipe not found or not owned by this user.");
            }
            String imageUrl = imageService.uploadImage(file);
            userRecipe.setImage_url(imageUrl);
            userRecipeRepository.save(userRecipe);
            return ResponseEntity.ok("Image changed and added to user recipe successfully. Image URL: " + imageUrl);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }

}
