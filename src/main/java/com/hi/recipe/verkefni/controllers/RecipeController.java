package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.*;
import com.hi.recipe.verkefni.services.CategoryService;
import com.hi.recipe.verkefni.services.RecipeService;
import com.hi.recipe.verkefni.services.SubcategoryService;
import com.hi.recipe.verkefni.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final SubcategoryService subcategoryService;

    public RecipeController(RecipeService recipeService, UserService userService, CategoryService categoryService, SubcategoryService subcategoryService){
        this.recipeService = recipeService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.subcategoryService = subcategoryService;
    }

    //================================================================================
    // GET Methods
    //================================================================================

    /**
     * Retrieves recipes with optional search and tag filtering
     * @param query Optional search term to filter recipes by title
     * @param tags Optional set of RecipeTags to filter recipes
     * @return Filtered list of recipes, or all recipes if no filters applied
     */
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes(
            @RequestParam(value="query", required = false) String query,
            @RequestParam(value="tags", required = false) Set<RecipeTag> tags){
        if ((query == null || query.isEmpty()) && tags == null ) {
            return ResponseEntity.ok(recipeService.findAllPaginated());
        } else if (tags == null || tags.isEmpty()) {
            return ResponseEntity.ok(recipeService.findByTitleContainingIgnoreCase(query));
        } else if (query == null || query.isEmpty()) {
            return  ResponseEntity.ok(recipeService.findByTagsIn(tags));
        }
        return ResponseEntity.ok(recipeService.findByTitleAndTags(query, tags));
    }

    /**
     * Retrieves a specific recipe by its identifier
     * @param id The unique identifier of the recipe
     * @return The requested recipe if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Recipe>> getRecipeById(@PathVariable int id){
        Optional<Recipe> recipe = recipeService.findById(id);
        return recipe.isPresent() ? ResponseEntity.ok(recipe) : ResponseEntity.notFound().build();
    }

    /**
     * Retrieves recipes with optional category filtering
     * @param categoryName  search term to filter recipes by category
     * @return Filtered list of recipes, or all recipes if no filters applied
     */
    @GetMapping("/recipes/byCategory")
    public ResponseEntity<List<Recipe>> getRecipesByCategory(
            @RequestParam("category") String categoryName) {

        System.out.println("Category name received: " + categoryName);  // This will print the category name
        if (categoryName == null || categoryName.isEmpty()) {
            return ResponseEntity.ok(recipeService.findAll()); // Or any fallback
        }

        List<Recipe> recipes = recipeService.findByCategory(categoryName);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Retrieves all recipes sorted by their creation date
     * @return List of recipes ordered by date, newest first
     */
    @GetMapping("/byDate")
    public ResponseEntity<List<Recipe>> getRecipesByDate(){
        return ResponseEntity.ok(recipeService.findByDate());
    }

    /**
     * Retrieves the featured recipes list from a predefined user
     * @return List of recipes marked as featured
     */
    @GetMapping("/featured")
    public ResponseEntity<List<Recipe>> getFeaturedRecipes(){
        User u = userService.findById(2).get();
        return ResponseEntity.ok(u.getFavourites());
    }


    @GetMapping("/highestRated")
    public List<Recipe> getRecipesByHighestRating() {

        return recipeService.findAllByAverageRatingDesc();
    }

    // Get recipes by subcategory
    @GetMapping("/bySubcategory")
    public ResponseEntity<List<Recipe>> getRecipesBySubcategory(@RequestParam("subcategoryId") Long subcategoryId) {
        List<Recipe> recipes = recipeService.findBySubcategoryId(subcategoryId);
        return ResponseEntity.ok(recipes);
    }

    //================================================================================
    // POST Methods
    //================================================================================
    
    /**
     * Creates a new recipe in the system
     * @param recipe The recipe object containing all required recipe data
     * @return Success message with 201 status, or error if creation fails
     */
    @PostMapping("/newRecipe")
    public ResponseEntity<String> addANewRecipe(@RequestBody Recipe recipe) {
        try {
            for (RecipeTag tag : recipe.getTags()) {
                // Throws IllegalArgumentException if the tag is invalid
                RecipeTag.valueOf(tag.name());
            }
            recipeService.save(recipe);
            return ResponseEntity.status(HttpStatus.CREATED).body("Recipe added successfully!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid RecipeTag value provided.");
        }
    }

    /**
     * Adds a recipe to the current user's favorites list
     * @param id The ID of the recipe to be added to favorites
     * @param session The current HTTP session containing user information
     * @return Success message if added, error if user not found or recipe doesn't exist
     */
    @PostMapping("/{id}/addAsFav")
    public ResponseEntity<String> addRecipeToFav(@PathVariable int id, HttpSession session){
        Optional<Recipe> or = recipeService.findById(id);
        Recipe recipe;
        if(or.isPresent()){
            recipe = or.get();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Problem adding favourite");
        }
        User user = (User)session.getAttribute("user");
        if(user != null){
            user.setFavourites(recipe);
            userService.save(user);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Favourite added to logged in user");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not update user");
    }

    @PostMapping("/auli/contact_us")
    public ResponseEntity<String> submitContactForm(@Valid @RequestBody ContactForm contactForm) {
        // Process form data here, e.g., save to database or send an email
        // For this example, we'll just return a confirmation message

        return ResponseEntity.status(200).body("Contact form submitted successfully!");
    }

    /**
     * Adds a rating to an existing recipe
     * @param id The ID of the recipe to modify
     * @param score The score to add to the recipe
     * @return Success message if tag added, error if recipe not found
     */
    @PostMapping("/{id}/addTempRating")
    public ResponseEntity<String> addTempRating(@PathVariable int id, @RequestParam @Min(1) @Max(5) int score) {
        try {
            // Call the service to add rating
            recipeService.addTempRatingToRecipe(id, score);
            return ResponseEntity.status(HttpStatus.CREATED).body("Rating successfully added");
        } catch (NoSuchElementException e) {
            // Return 404 if the recipe is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Catch any other exceptions (e.g., database issues)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    //================================================================================
    // PATCH Methods
    //================================================================================
    
    /**
     * Updates an existing recipe's information
     * @param id The ID of the recipe to update
     * @param recipe The updated recipe data
     * @return Success message if updated, error if recipe not found
     */
    @PatchMapping("{id}")
    public ResponseEntity<String> updateRecipe(@PathVariable int id, @RequestBody Recipe recipe){
        Optional<Recipe> or = recipeService.findById(id);
        if(or.isPresent()) {
            Recipe excistingRecipe = or.get();
            excistingRecipe.setTitle(recipe.getTitle());
            excistingRecipe.setDescription(recipe.getDescription());
            excistingRecipe.setAverageRating(recipe.getAverageRating());
            recipeService.save(recipe);
            return ResponseEntity.ok("Recipe updated!");
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("Could not update recipe");
    }


    /**
     * Adds a new tag to an existing recipe
     * @param id The ID of the recipe to modify
     * @param tag The tag to add to the recipe
     * @return Success message if tag added, error if recipe not found
     */
    @PatchMapping("/{id}/addTag")
    public ResponseEntity<String> addTagToRecipe(@PathVariable int id, @RequestParam RecipeTag tag) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            recipe.getTags().add(tag);
            recipeService.save(recipe);
            return ResponseEntity.ok("Tag added successfully to the recipe.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found.");
    }

    /**
     * Adds a new tag to an existing recipe
     * @param id The ID of the recipe to modify
     * @param category The tag to add to the recipe
     * @return Success message if tag added, error if recipe not found
     */
    @PatchMapping("/{id}/addCategory")
    public ResponseEntity<String> addCategoryToRecipe(@PathVariable int id, @RequestParam Category category) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            recipe.getCategories().add(category);  // Assuming recipe has a set of categories
            recipeService.save(recipe);
            return ResponseEntity.ok("Category added successfully to the recipe.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found.");
    }

    //================================================================================
    // DELETE Methods
    //================================================================================
    
    /**
     * Removes a recipe from the system
     * @param id The ID of the recipe to delete
     * @return Success message if deleted
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable int id){
        recipeService.deleteById(id);
        return ResponseEntity.status(200).body("Deleted recipe");
    }
}