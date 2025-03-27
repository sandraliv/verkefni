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
            // Find the recipe by ID
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

            // List to store image URLs (preserves order, allows duplicates)
            List<String> imageUrls = new ArrayList<>();

            // Upload images to cloudinary and get URLs
            for (MultipartFile file : files) {
                String imageUrl = imageService.uploadImage(file); // Upload each file
                imageUrls.add(imageUrl); // Add the URL of the uploaded image to the list
            }

            // Save the image URLs to the recipe (if there are existing images, add new ones)
            if (recipe.getImageUrls() == null) {
                recipe.setImageUrls(imageUrls); // If no images, set the list of URLs
            } else {
                recipe.getImageUrls().addAll(imageUrls); // Append the new images to the existing ones
            }

            // Save the updated recipe to the database
            recipeRepository.save(recipe);

            // Return success response
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
            // Find the user recipe by both user recipe ID and user ID to ensure the user is authorized
            UserRecipe userRecipe = userRecipeRepository.findByIdAndUserId(user_recipe_id, userId);

            // If no recipe is found, return 404
            if (userRecipe == null) {
                return ResponseEntity.status(404).body("Recipe not found or not owned by this user.");
            }

            // List to store the image URLs
            List<String> imageUrls = new ArrayList<>();

            // Loop through each file, upload the image, and collect the URLs
            for (MultipartFile file : files) {
                // Upload the image to Cloudinary (or other service) and get the URL
                String imageUrl = imageService.uploadImage(file);

                // Add the image URL to the list
                imageUrls.add(imageUrl);
            }

            // Add the list of new image URLs to the recipe
            if (userRecipe.getImageUrls() == null) {
                userRecipe.setImageUrls(imageUrls);  // If no images exist, set the list
            } else {
                userRecipe.getImageUrls().addAll(imageUrls);  // Otherwise, append the new images to the existing ones
            }

            // Save the updated recipe back to the database
            userRecipeRepository.save(userRecipe);

            // Return a success message with the list of image URLs
            return ResponseEntity.ok("Images uploaded and added to user recipe successfully. Image URLs: " + imageUrls);

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
            // Fetch the recipe by ID or throw an exception if not found
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

            // Clear existing image URLs to replace them
            recipe.setImageUrls(new ArrayList<>());  // Clear the existing images (using List to maintain order)

            // List to store URLs of the uploaded images
            List<String> imageUrls = new ArrayList<>();

            // Process each uploaded file and upload it to the image service
            for (MultipartFile file : files) {
                String imageUrl = imageService.uploadImage(file);
                imageUrls.add(imageUrl);  // Add the image URL to the list
            }

            // Set the list of image URLs to the recipe
            recipe.setImageUrls(imageUrls);

            // Save the updated recipe
            recipeRepository.save(recipe);

            // Return success response
            return ResponseEntity.ok("Images replaced successfully for recipe with ID " + recipeId + ". Image URLs: " + imageUrls);

        } catch (IOException e) {
            // Handle any IO exceptions that occur during image upload
            return ResponseEntity.status(500).body("Error uploading images: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            // Handle the case where the recipe is not found
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PatchMapping("/{userId}/{user_recipe_id}/replaceUserImg")
    public ResponseEntity<String> replaceUserRecipeImages(@PathVariable int user_recipe_id,
                                                          @RequestParam("files") List<MultipartFile> files,
                                                          @PathVariable int userId) {
        try {
            // Find the user recipe by both user recipe ID and user ID to ensure the user is authorized
            UserRecipe userRecipe = userRecipeRepository.findByIdAndUserId(user_recipe_id, userId);

            // If no recipe is found, return 404
            if (userRecipe == null) {
                return ResponseEntity.status(404).body("Recipe not found or not owned by this user.");
            }

            // Clear existing image URLs to replace them
            userRecipe.setImageUrls(new ArrayList<>());  // Clear the existing images

            // List to store the image URLs
            List<String> imageUrls = new ArrayList<>();

            // Loop through each file and upload the image
            for (MultipartFile file : files) {
                // Upload the image to Cloudinary (or other service) and get the URL
                String imageUrl = imageService.uploadImage(file);

                // Add the image URL to the list
                imageUrls.add(imageUrl);
            }

            // Replace the image URLs of the user recipe
            userRecipe.setImageUrls(imageUrls); // Set the new list of image URLs

            // Save the updated recipe back to the database
            userRecipeRepository.save(userRecipe);

            // Return a success message with the list of image URLs
            return ResponseEntity.ok("Images replaced and added to user recipe successfully. Image URLs: " + imageUrls);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading images: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }
}
