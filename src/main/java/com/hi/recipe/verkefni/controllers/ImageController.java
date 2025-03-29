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
import java.util.ArrayList;
import java.util.List;

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
     * @param files Myndirnar sem á að bæta við uppskrift jpeg/jpg
     * @return skilaboð að allt hafi gengið eftir eða villu
     */
    @PostMapping("/{recipeId}/upload")
    public ResponseEntity<String> uploadImagesToRecipe(@PathVariable int recipeId, @RequestParam("files") List<MultipartFile> files) {
        try {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String imageUrl = imageService.uploadImage(file);
                imageUrls.add(imageUrl);
            }

            if (recipe.getImageUrls() == null) {
                recipe.setImageUrls(imageUrls);
            } else {
                recipe.getImageUrls().addAll(imageUrls);
            }
            recipeRepository.save(recipe);
            return ResponseEntity.ok("Images uploaded and added to recipe successfully. Image URLs: " + imageUrls);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading images: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/{userId}/{user_recipe_id}/upload")
    public ResponseEntity<String> uploadImagesToUserRecipe(@PathVariable int user_recipe_id,
                                                           @RequestParam("files") List<MultipartFile> files,
                                                           @PathVariable int userId) {
        try {
            UserRecipe userRecipe = userRecipeRepository.findByIdAndUserId(user_recipe_id, userId);
            if (userRecipe == null) {
                return ResponseEntity.status(404).body("Recipe not found or not owned by this user.");
            }
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String imageUrl = imageService.uploadImage(file);
                imageUrls.add(imageUrl);
            }
            if (userRecipe.getImageUrls() == null) {
                userRecipe.setImageUrls(imageUrls);
            } else {
                userRecipe.getImageUrls().addAll(imageUrls);
            }
            userRecipeRepository.save(userRecipe);
            return ResponseEntity.ok("Images posted successfully for recipe with ID " + user_recipe_id + ". Image URLs: " + imageUrls + "for User" + userId);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading images: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }

    /**
     * @param recipeId Id á uppskrift sem á að breyta, eyðir fyrst myndunum og setur nyjar
     * @param files Myndin sem á að setja á uppskrift jpeg/jpg
     * @return skilaboð að allt hafi gengið eftir eða villu
     */
    @PatchMapping("/{recipeId}/replaceRecipeImg")
    public ResponseEntity<String> replaceRecipeImages(@PathVariable int recipeId, @RequestParam("files") List<MultipartFile> files) {
        try {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

            recipe.setImageUrls(new ArrayList<>());

            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile file : files) {
                String imageUrl = imageService.uploadImage(file);
                imageUrls.add(imageUrl);
            }

            recipe.setImageUrls(imageUrls);
            recipeRepository.save(recipe);
            return ResponseEntity.ok("Images replaced successfully for recipe with ID " + recipeId + ". Image URLs: " + imageUrls);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading images: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PatchMapping("/{userId}/{user_recipe_id}/replaceUserImg")
    public ResponseEntity<String> replaceUserRecipeImages(@PathVariable int user_recipe_id,
                                                          @RequestParam("files") List<MultipartFile> files,
                                                          @PathVariable int userId) {
        try {
            UserRecipe userRecipe = userRecipeRepository.findByIdAndUserId(user_recipe_id, userId);
            if (userRecipe == null) {
                return ResponseEntity.status(404).body("Recipe not found or not owned by this user.");
            }
            userRecipe.setImageUrls(new ArrayList<>());
            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile file : files) {
                String imageUrl = imageService.uploadImage(file);
                imageUrls.add(imageUrl);
            }
            userRecipe.setImageUrls(imageUrls);
            userRecipeRepository.save(userRecipe);

            return ResponseEntity.ok("Images replaced successfully for recipe with ID " + user_recipe_id + ". Image URLs: " + imageUrls + "for User" + userId);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading images: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }
}
