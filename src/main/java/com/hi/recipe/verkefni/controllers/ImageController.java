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
@RequestMapping("/recipes")
public class ImageController {

    private final ImageService imageService;
    private final RecipeRepository recipeRepository;

    @Autowired
    public ImageController(ImageService imageService, RecipeRepository recipeRepository) {
        this.imageService = imageService;
        this.recipeRepository = recipeRepository;
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
}