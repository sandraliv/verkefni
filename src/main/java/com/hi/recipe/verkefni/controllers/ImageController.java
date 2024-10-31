package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import com.hi.recipe.verkefni.services.ImageService;
import com.hi.recipe.verkefni.services.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;
    private final RecipeRepository recipeRepository;

    @Autowired
    public ImageController(ImageService imageService, RecipeRepository recipeRepository) {
        this.imageService = imageService;
        this.recipeRepository = recipeRepository;
    }

    @PostMapping("/{recipeId}/upload")
    public ResponseEntity<String> uploadImageToRecipe(@PathVariable int recipeId, @RequestParam("file") MultipartFile file) {
        try {
            // Step 1: Find the Recipe by ID
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

            // Step 2: Upload the image to Cloudinary and get the URL
            String imageUrl = imageService.uploadImage(file);

            // Step 3: Update the Recipe's imageUrl field
            recipe.setImage_url(imageUrl);

            // Step 4: Save the updated Recipe
            recipeRepository.save(recipe);

            return ResponseEntity.ok("Image uploaded and added to recipe successfully. Image URL: " + imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}