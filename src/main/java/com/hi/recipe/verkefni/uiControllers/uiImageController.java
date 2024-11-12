package com.hi.recipe.verkefni.uiControllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import com.hi.recipe.verkefni.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
     *
     * @param recipeId Id of the recipe which picture is missing
     * @param model the model
     * @return uploadImage.html
     */
    @GetMapping("/{recipeId}/upload")
    public String showUploadForm(@PathVariable int recipeId, Model model) {
        model.addAttribute("recipeId", recipeId);
        return "uploadImage"; // uploadImage.html 
    }

    //================================================================================
    // POST Methods
    //================================================================================

    /**
     * @param recipeId Id of the recipe to which the image should be added
     * @param file the image to upload to the recipe (jpeg/jpg)
     * @param model the model
     * @return uploadImage.html
     */
    @PostMapping("/{recipeId}/upload")
    public String uploadImageToRecipe(@PathVariable int recipeId, @RequestParam("file") MultipartFile file, Model model) {
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
            model.addAttribute("message", "Image uploaded and added to recipe successfully");
            model.addAttribute("imageUrl", imageUrl);
            return "uploadImage"; 
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Error uploading image: " + e.getMessage());
            return "uploadImage"; 
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "uploadImage";
        }
    }

    //================================================================================
    // PATCH Methods
    //================================================================================

    /**
     * @param recipeId Id of the recipe to be modified
     * @param file the image to be set on the recipe jpeg/jpg
     * @param model the model
     * @return uploadImage.html
     */
    @PatchMapping("/{recipeId}/replaceImg")
    public String replaceRecipeImage(@PathVariable int recipeId, @RequestParam("file") MultipartFile file, Model model) {
        try {
            Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new IllegalArgumentException("Recipe not found"));
            String imageUrl = imageService.uploadImage(file);
            recipe.setImage_url(imageUrl);
            recipeRepository.save(recipe);
            model.addAttribute("message", "Image changed, uploaded and added to recipe successfully");
            model.addAttribute("imageUrl", imageUrl);
            return "uploadImage";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Error uploading image: " + e.getMessage());
            return "uploadImage";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "uploadImage";
        }
    }
}
