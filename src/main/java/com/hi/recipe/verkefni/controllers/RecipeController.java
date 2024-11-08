package com.hi.recipe.verkefni.controllers;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import com.hi.recipe.verkefni.services.RecipeService;
import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;
    private final UserService userService;

    public RecipeController(RecipeService recipeService, UserService userService){
        this.recipeService = recipeService;
        this.userService = userService;
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
        recipeService.save(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body("Recipe added successfully!");
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