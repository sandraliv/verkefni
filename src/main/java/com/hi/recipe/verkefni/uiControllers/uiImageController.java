package com.hi.recipe.verkefni.uiControllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import com.hi.recipe.verkefni.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("recipesui")
public class uiImageController {

    private final ImageService imageService;
    private final RecipeRepository recipeRepository;

    @Autowired
    public uiImageController(ImageService imageService, RecipeRepository recipeRepository) {
        this.imageService = imageService;
        this.recipeRepository = recipeRepository;
    }

    //================================================================================
    // GET Methods
    //================================================================================

    /**
     * @param recipeId Id of the recipe which picture is missing
     * @param model    the model
     * @return uploadImage.html
     */
    @GetMapping("/{recipeId}/upload")
    public String showUploadForm(@PathVariable("recipeId") int recipeId, Model model) {
        model.addAttribute("id", recipeId);
        return "uploadPhoto"; // Returns the uploadPhoto.html view
    }

    //================================================================================
    // POST Methods
    //================================================================================

    /**
     * @param recipeId Id of the recipe to which the image should be added
     * @param files     the image to upload to the recipe (jpeg/jpg)
     * @param model    the model
     * @return uploadImage.html
     */
    @PostMapping("/{recipeId}/upload")
    public String uploadImagesToRecipe(@PathVariable int recipeId,
                                       @RequestParam("files") List<MultipartFile> files,
                                       Model model) {
        try {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

            if (files == null || files.isEmpty()) {
                model.addAttribute("message", "No images uploaded, but recipe was found successfully.");
                return "redirect:/recipeList";
            }

            // Change from Set to List to allow duplicates and preserve order
            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String imageUrl = imageService.uploadImage(file);
                    imageUrls.add(imageUrl); // Add URL to list
                }
            }

            // Append the new image URLs to the existing ones
            if (recipe.getImageUrls() == null) {
                recipe.setImageUrls(imageUrls);
            } else {
                recipe.getImageUrls().addAll(imageUrls); // Append new URLs to the existing list
            }

            recipeRepository.save(recipe);

            model.addAttribute("message", "Images uploaded and added to recipe successfully");

            return "redirect:/recipeList";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Error uploading images: " + e.getMessage());
            return "uploadPhoto";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "uploadPhoto";
        }

    }



    //================================================================================
    // PATCH Methods
    //================================================================================

    /**
     * @param recipeId Id of the recipe to be modified
     * @param files     the image to be set on the recipe jpeg/jpg
     * @param model    the model
     * @return uploadImage.html
     */
    @PatchMapping("/{recipeId}/replaceImgs")
    public String replaceRecipeImages(@PathVariable int recipeId,
                                      @RequestParam("files") List<MultipartFile> files,
                                      Model model) {
        try {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

            // Change from Set to List to allow duplicates and preserve order
            List<String> imageUrls = new ArrayList<>();

            for (MultipartFile file : files) {
                String imageUrl = imageService.uploadImage(file);
                imageUrls.add(imageUrl); // Add URL to list
            }

            // Replace the image URLs in the recipe (clear existing ones first)
            recipe.setImageUrls(imageUrls); // Set the new list of image URLs

            // Save the updated recipe
            recipeRepository.save(recipe);

            model.addAttribute("message", "Images uploaded and added to recipe successfully.");
            model.addAttribute("imageUrls", imageUrls);

            return "uploadImage";

        } catch (IOException e) {
            model.addAttribute("errorMessage", "Error uploading images: " + e.getMessage());
            return "uploadImage";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "uploadImage";
        }
    }
}
