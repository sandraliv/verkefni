package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.*;
import com.hi.recipe.verkefni.services.RecipeService;
import com.hi.recipe.verkefni.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final UserService userService;


    public RecipeController(RecipeService recipeService, UserService userService) {
        this.recipeService = recipeService;
        this.userService = userService;

    }

    //================================================================================
    // GET Methods
    //================================================================================

    /**
     * Retrieves recipes with optional search and tag filtering
     *
     * @param query Optional search term to filter recipes by title
     * @param tags  Optional set of RecipeTags to filter recipes
     * @return Filtered list of recipes, or all recipes if no filters applied
     */
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "tags", required = false) Set<RecipeTag> tags) {
        if ((query == null || query.isEmpty()) && tags == null) {
            return ResponseEntity.ok(recipeService.findAllPaginated());
        } else if (tags == null || tags.isEmpty()) {
            return ResponseEntity.ok(recipeService.findByTitleContainingIgnoreCase(query));
        } else if (query == null || query.isEmpty()) {
            return ResponseEntity.ok(recipeService.findByTagsIn(tags, 0, 10));
        }


        return ResponseEntity.ok(recipeService.findByTitleAndTags(query, tags));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Recipe>> getAllRecipes(
            @RequestParam(value = "sort", required = false, defaultValue = "RATING") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        long startTime = System.currentTimeMillis();
        // Parse the sort parameter to SortType enum
        SortType sortType = SortType.valueOf(sort.toUpperCase());

        // Fetch recipes with pagination and sorting
        List<Recipe> recipes = recipeService.findAllPaginated(page, size, sortType);

        // Log the image URLs for each recipe
        for (Recipe recipe : recipes) {
            // Assuming Recipe has a method getImageUrls() that returns a List<String>
            List<String> imageUrls = recipe.getImageUrls();

            if (imageUrls == null || imageUrls.isEmpty()) {
                System.out.println("Recipe ID: " + recipe.getId() + " has no image URLs.");
            } else {
                // Log each image URL in the list
                for (String url : imageUrls) {
                    if (url == null || url.isEmpty()) {
                        System.out.println("Recipe ID: " + recipe.getId() + " has an empty image URL.");
                    } else {
                        System.out.println("Recipe ID: " + recipe.getId() + " has image URL: " + url);
                    }
                }
            }
        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("⏱️ Time taken to fetch recipes: " + elapsedTime + " ms");
        return ResponseEntity.ok(recipes);

    }

    /**
     * Retrieves recipes with optional category filtering
     *
     * @param categories search term to filter recipes by category
     * @return Filtered list of recipes, or all recipes if no filters applied
     */
    @GetMapping("/byCategoryOLD")
    public ResponseEntity<List<Recipe>> getRecipesByCategory(
            @RequestParam(value = "categories", required = false) Set<Category> categories) {
        // If no category is provided, return all recipes
        if (categories == null) {
            return ResponseEntity.ok(recipeService.findAll());
        }
        List<Recipe> recipes = recipeService.findByCategoriesIn(categories, 0, 10);
        return ResponseEntity.ok(recipes);
    }

    /**
     * Retrieves recipes filtered by category with optional sorting (rating or date).
     *
     * @param categories The categories to filter the recipes by.
     * @param sort       The sorting parameter (either "rating" or "date").
     * @param page       The page number for pagination.
     * @param size       The number of items per page.
     * @return A filtered and sorted list of recipes.
     */
    @GetMapping("/byCategory")
    public ResponseEntity<List<Recipe>> getRecipesByCategory(
            @RequestParam(value = "categories") Set<Category> categories,
            @RequestParam(value = "sort", required = false, defaultValue = "averageRating") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        // Parse the sort parameter and convert it to SortType enum
        SortType sortType = SortType.valueOf(sort.toUpperCase());

        // Fetch recipes by categories with sorting and pagination
        List<Recipe> recipes = recipeService.findByCategoriesIn(categories, page, size, sortType);

        return ResponseEntity.ok(recipes);
    }


    /**
     * Retrieves all recipes sorted by their creation date
     *
     * @return List of recipes ordered by date, newest first
     */
    @GetMapping("/byDate")
    public ResponseEntity<List<Recipe>> getRecipesByDate(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(recipeService.findByDate(page, size));
    }

    /**
     * Retrieves the featured recipes list from a predefined user
     *
     * @return List of recipes marked as featured
     */
    @GetMapping("/featured")
    public ResponseEntity<Set<Recipe>> getFeaturedRecipes() {
        User u = userService.findById(2).get();
        return ResponseEntity.ok(u.getFavourites());
    }

    /**
     * Retrieves all recipes sorted by their average rating
     *
     * @return List of recipes ordered by rating, highest first
     */
    @GetMapping("/highestRated")
    public ResponseEntity<List<Recipe>> getRecipesByHighestRating(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(recipeService.findAllByAverageRatingDesc(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Recipe>> getRecipeById(@PathVariable int id) {
        Optional<Recipe> recipe = recipeService.findById(id);
        return recipe.isPresent() ? ResponseEntity.ok(recipe) : ResponseEntity.notFound().build();
    }


    //================================================================================
    // POST Methods
    //================================================================================

    /**
     * Creates a new recipe in the system
     *
     * @param recipe The recipe object containing all required recipe data
     * @return Success message with 201 status, or error if creation fails
     */
    @PostMapping("/newRecipe")
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        // Save the recipe
        Recipe savedRecipe = recipeService.save(recipe);

        // Log the ID and other relevant details
        System.out.println("Saved Recipe with ID: " + savedRecipe.getId());
        System.out.println("Title: " + savedRecipe.getTitle());
        System.out.println("Description: " + savedRecipe.getDescription());
        System.out.println("Ingredients: " + savedRecipe.getIngredients());

        // Return a response with the saved recipe
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe);
    }

    @PostMapping("/{id}/addAsFav")
    @Transactional
    public ResponseEntity<String> addRecipeToFav(@PathVariable int id, @RequestParam int userId) {
        Optional<Recipe> recipeOptional = recipeService.findById(id);
        if (recipeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found");
        }
        Recipe recipe = recipeOptional.get();
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        User user = userOptional.get();

        if (user.getFavourites().contains(recipe)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Recipe is already in favorites");
        }

        try {
            boolean success = recipeService.addRecipeToFavorites(recipe, user);
            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Recipe added to favorites");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add recipe to favorites");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while adding to favorites");
        }
    }


    @PostMapping("/auli/contact_us")
    public ResponseEntity<String> submitContactForm(@Valid @RequestBody ContactForm contactForm) {
        // Process form data here, e.g., save to database or send an email
        // For this example, we'll just return a confirmation message

        return ResponseEntity.status(200).body("Contact form submitted successfully!");
    }

    /**
     * Adds a rating to an existing recipe.
     *
     * @param recipeId The ID of the recipe to rate
     * @param userId   The ID of the user submitting the rating
     * @param score    The score to add to the recipe (between 1 and 5)
     * @return Success message if the rating is added, error message if the recipe or user not found, or if there's an internal error
     */

    @PostMapping("/{id}/addRating")
    public ResponseEntity<String> addRatingToRecipeRest(
            @PathVariable("id") int recipeId,
            @RequestParam("userId") int userId,
            @RequestParam("score") @Min(1) @Max(5) int score) {

        System.out.println("Looking for recipe with ID: " + recipeId);

        // Find the recipe by ID
        Optional<Recipe> recipeOptional = recipeService.findById(recipeId);
        if (recipeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found");
        }

        // Find the user by ID
        Optional<User> userOpt = userService.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOpt.get();

        // Check if the user has already rated this recipe
        if (user.getUserRatings().containsKey(recipeOptional.get())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User has already rated this recipe");
        }

        // Add the rating through the service layer
        try {
            recipeService.addRating(recipeId, user, score); // This calls your service layer method
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Rating submitted by " + user.getUsername());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while submitting your rating");
        }
    }


    // Endpoint to remove a rating from a recipe
    @PostMapping("/{id}/removeRating")
    public ResponseEntity<String> removeRating(
            @PathVariable("id") int recipeId) {  // Recipe ID to remove rating from

        try {
            // Call the service to remove the rating
            recipeService.removeRatingFromRecipe(recipeId);
            return ResponseEntity.status(HttpStatus.OK).body("Rating removed successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }


    //================================================================================
    // PATCH Methods
    //================================================================================

    /**
     * Updates an existing recipe's information
     *
     * @param id     The ID of the recipe to update
     * @param recipe The updated recipe data
     * @return Success message if updated, error if recipe not found
     */

    @PatchMapping("{id}")
    public ResponseEntity<String> updateRecipe(@PathVariable int id, @RequestBody Recipe recipe) {
        Optional<Recipe> or = recipeService.findById(id);
        if (or.isPresent()) {
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
     *
     * @param id  The ID of the recipe to modify
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
     * Adds a new category to an existing recipe
     *
     * @param id       The ID of the recipe to modify
     * @param category The category to add to the recipe
     * @return Success message if tag added, error if recipe not found
     */
    @PatchMapping("/{id}/addCategory")
    public ResponseEntity<String> addTagToRecipe(@PathVariable int id, @RequestParam Category category) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            recipe.getCategories().add(category);
            recipeService.save(recipe);
            return ResponseEntity.ok("Category added successfully to the recipe.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found.");
    }

    @PatchMapping("/{id}/recalculateAverageRating")
    public ResponseEntity<String> recalculateAverageRating(@PathVariable("id") int recipeId) {
        // Fetch the recipe from the database
        Optional<Recipe> optionalRecipe = recipeService.findById(recipeId);

        if (optionalRecipe.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found");
        }

        Recipe recipe = optionalRecipe.get();

        // Recalculate the average rating
        recipe.recalculateAverageRating();

        // Save the updated recipe with the new average rating
        recipeService.save(recipe);

        return ResponseEntity.ok("Average rating recalculated successfully.");
    }
    //================================================================================
    // DELETE Methods
    //================================================================================

    /**
     * Removes a recipe from the system
     *
     * @param id The ID of the recipe to delete
     * @return Success message if deleted
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable int id) {
        recipeService.deleteById(id);
        return ResponseEntity.status(200).body("Deleted recipe");
    }

    /**
     * Removes a recipe from the current user's favorites list.
     *
     * @param recipeId The ID of the recipe to remove from favorites
     * @param userId   The ID of the logged-in user
     * @return Success message if removed, 404 if recipe or user not found
     */
    @DeleteMapping("/removeFavorite/{recipeId}")
    public ResponseEntity<String> removeFavoriteRecipe(@PathVariable int recipeId, @RequestParam int userId) {
        // Find the user by userId (the ID of the logged-in user)
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = userOptional.get();

        // Find the recipe by ID
        Optional<Recipe> recipeOptional = recipeService.findById(recipeId);
        if (recipeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found.");
        }

        Recipe recipe = recipeOptional.get();

        // Use the service method to remove the recipe from the user's favorites
        boolean success = recipeService.removeRecipeFromFavorites(recipe, user);

        if (success) {
            return ResponseEntity.ok("Recipe removed from favorites.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found in favorites.");
        }
    }

}
