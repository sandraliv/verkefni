package com.hi.recipe.verkefni.uiControllers;

import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.services.RecipeService;
import com.hi.recipe.verkefni.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("allrecipes")
public class RecipeControllerui {
    private final RecipeService recipeService;
    private final UserService userService;


    @Autowired
    public RecipeControllerui(RecipeService recipeService, UserService userService) {
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
    @GetMapping("/all")
    public String getAllRecipes(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "tags", required = false) Set<String> tags, HttpSession session,
            Model model) {
        Set<RecipeTag> tagsEnumSet = recipeService.convertToRecipeTagEnum(tags);
        List<Recipe> recipes = recipeService.filterRecipes(query, tagsEnumSet);
        User user = (User) session.getAttribute("user");
        if (user != null) {
            // Use the method to get recipes with the favorited flag
            recipes = recipeService.getRecipesWithFavoritedFlag(user);

            // Add the user to the model to be used in the view
            model.addAttribute("user", user);
        }

        for (Recipe recipe : recipes) {
            String formattedDate = recipeService.formatDate(recipe.getDateAdded());
            recipe.setFormattedDate(formattedDate);  // Add formatted date to recipe
        }

        model.addAttribute("allTags",RecipeTag.values());
        model.addAttribute("recipes", recipes);
        return "recipeList"; //recipeList.html
    }

    /**
     * Retrieves a specific recipe by its identifier
     * @param id The unique identifier of the recipe
     * @return The requested recipe if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public String getRecipeById(@PathVariable int id, Model model) {
        Optional<Recipe> recipe = recipeService.findById(id);
        if (recipe.isPresent()) {
            model.addAttribute("recipe", recipe.get());
            return "recipeList";
        }
        model.addAttribute("errorMessage", "Recipe not found.");
        return "error";
    }

    /**
     * @param recipe New recipe object
     * @param model The model
     * @return addRecipe.html
     */
    @GetMapping("/addRecipe")
    public String addNewRecipe(@ModelAttribute("recipe") Recipe recipe, Model model) {
        model.addAttribute("newRecipe", new Recipe());
        model.addAttribute("allTags", RecipeTag.values()); // Pass all enum values
        model.addAttribute("allCategories",Category.values());
        return "addRecipe";
    }

    /**
     * Retrieves all recipes sorted by their creation date
     * @return List of recipes ordered by date, newest first
     */
    @GetMapping("/byDate")
    public String getRecipesByDate(Model model) {
        List<Recipe> recipes = recipeService.findByDate();
        for (Recipe recipe : recipes) {
            String formattedDate = recipeService.formatDate(recipe.getDateAdded());
            recipe.setFormattedDate(formattedDate);  // Add formatted date to recipe
        }

        model.addAttribute("recipes", recipes);
        return "recipeList";
    }

    @GetMapping("/byCategory")
    public String getRecipesByCategory(
            @RequestParam(value = "categories", required = false) Set<String> categories,
            @RequestParam(value = "sort", required = false, defaultValue = "byDate") String sort, // Default sort is "byDate"
            Model model) {

        List<Recipe> recipes;
        // Convert the category strings to Category enum
        Set<Category> categoryEnumSet = recipeService.convertToCategoryEnum(categories);
        // Get sorted recipes based on the selected sort option and category filter
        recipes = recipeService.getSortedRecipes(sort, categoryEnumSet);
        for (Recipe recipe : recipes) {
            String formattedDate = recipeService.formatDate(recipe.getDateAdded());
            recipe.setFormattedDate(formattedDate);  // Add formatted date to recipe
        }
        // Add attributes to the model
        model.addAttribute("tags", RecipeTag.values());  // Pass all tags to the view
        model.addAttribute("recipes", recipes);  // Add the list of recipes
        model.addAttribute("categories", Category.values());  // Add all categories to the view
        model.addAttribute("sort", sort);  // Keep track of the selected sort option in the model
        model.addAttribute("selectedCategories", categories);  // Keep track of selected categories
        return "byCategory";  // Return to the 'byCategory' template
    }

    /**
     * Retrieves the featured recipes list from a predefined user
     * @return List of recipes marked as featured
     */
    @GetMapping("/featured")
    public String getFeaturedRecipes(Model model) {
        Optional<User> user = userService.findById(2);
        if (user.isPresent()) {
            model.addAttribute("recipes", user.get().getFavourites());
            return "recipeList";
        }
        model.addAttribute("errorMessage", "User not found for featured recipes.");
        return "error";
    }

    @GetMapping("/highestRated")
    public String getRecipesByHighestRating(Model model) {
        List<Recipe> recipes = recipeService.findAllByAverageRatingDesc();
        // Format the date before adding it to the model
        for (Recipe recipe : recipes) {
            String formattedDate = recipeService.formatDate(recipe.getDateAdded());
            recipe.setFormattedDate(formattedDate);  // Add formatted date to recipe
        }
        model.addAttribute("recipes", recipes);
        return "recipeList";
    }

    //================================================================================
    // POST Methods
    //================================================================================

    /**
     * Creates a new recipe in the system
     * @param recipe The recipe object containing all required recipe data
     * @return Redirects to recipe list with a success message
     */
    @PostMapping("/newRecipe")
    public String addANewRecipe(@ModelAttribute("recipe") Recipe recipe, RedirectAttributes redirectAttributes) {
        Recipe savedRecipe = recipeService.save(recipe);
        redirectAttributes.addAttribute("recipeId", savedRecipe.getId());
        return "redirect:/recipesui/{recipeId}/upload"; // Redirects to upload with recipeId
    }

    /**
     * Adds a recipe to the current user's favorites list
     * @param id The ID of the recipe to be added to favorites
     * @param session The current HTTP session containing user information
     * @return Redirects to favorites page with success or error message
     */
    @PostMapping("/{id}/addAsFav")
    public String addRecipeToFav(@PathVariable int id, HttpSession session, Model model) {
        Optional<Recipe> or = recipeService.findById(id);
        if (or.isEmpty()) {
            model.addAttribute("errorMessage", "Recipe not found.");
            return "error";
        }

        User user = (User) session.getAttribute("user");
        if (user != null) {
            user.setFavourites(or.get());
            userService.save(user);
            model.addAttribute("message", "Recipe added to favorites.");
            return "redirect:/usersui/favorites";
        }

        model.addAttribute("errorMessage", "User not logged in.");
        return "error";
    }

    /**
      * Adds a rating to a recipe.
      *
      * @param id The ID of the recipe to which the temporary rating is added
      * @param score The rating score to be added to the recipe
      * @param session The HTTP session containing the logged-in user
      * @param model The model to add attributes for rendering views
      * @return Redirects to the recipe list or the recipe detail page
     */
    @PostMapping("/{id}/addRating")
    public String addRatingToRecipeMvc(
            @PathVariable("id") int id,
            @RequestParam("score") @Min(1) @Max(5) int score,
            HttpSession session,
            Model model) {

        // Fetch the logged-in user from the session
        User user = (User) session.getAttribute("user");

        if (user == null) {
            model.addAttribute("errorMessage", "You must be logged in to rate a recipe.");
            return "login";  // Redirect to login page if user is not logged in
        }

        // Fetch the recipe
        Optional<Recipe> recipeOptional = recipeService.findById(id);
        if (recipeOptional.isEmpty()) {
            model.addAttribute("errorMessage", "Recipe not found.");
            return "recipeList";  // Return to recipe list if recipe is not found
        }

        // Get the recipe object
        Recipe recipe = recipeOptional.get();

        try {
            // Add the rating to the recipe
            recipeService.addRating(id, user, score);

            // Recalculate the average rating after adding the new score
            recipe.recalculateAverageRating();
            recipeService.save(recipe);

            model.addAttribute("successMessage", "Rating successfully added!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An error occurred while submitting your rating.");
        }

        // Redirect back to the recipe list or the recipe detail page
        return "redirect:/allrecipes/all";  // Or redirect to the recipe detail page
    }

//================================================================================
    // PATCH Methods
    //================================================================================

    /**
     * Updates an existing recipe's information
     * @param id The ID of the recipe to update
     * @param recipe The updated recipe data
     * @return Redirects to the updated recipe page with a success or error message
     */
    @PostMapping("{id}/update")
    public String updateRecipe(@PathVariable int id, @ModelAttribute("recipe") Recipe recipe, Model model) {
        Optional<Recipe> or = recipeService.findById(id);
        if (or.isPresent()) {
            Recipe existingRecipe = or.get();
            existingRecipe.setTitle(recipe.getTitle());
            existingRecipe.setDescription(recipe.getDescription());
            recipeService.save(existingRecipe);
            model.addAttribute("message", "Recipe updated!");
            return "redirect:/recipes/" + id;
        }
        model.addAttribute("errorMessage", "Could not update recipe.");
        return "error";
    }

    /**
     * Adds a new tag to an existing recipe
     * @param id The ID of the recipe to modify
     * @param tag The tag to add to the recipe
     * @return Redirects to the recipe page with a success or error message
     */
    @PostMapping("/{id}/addTag")
    public String addTagToRecipe(@PathVariable int id, @RequestParam RecipeTag tag, Model model) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            recipe.getTags().add(tag);
            recipeService.save(recipe);
            model.addAttribute("message", "Tag added successfully to the recipe.");
            return "redirect:/recipes/" + id;
        }
        model.addAttribute("errorMessage", "Recipe not found.");
        return "error";
    }

    @PatchMapping("/{id}/addCategory")
    public String addCategoryToRecipe(@PathVariable int id, @RequestParam Category category, Model model) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            recipe.getCategories().add(category);
            recipeService.save(recipe);
            return "redirect:/recipes/" + id; // Redirect to updated recipe
        }
        model.addAttribute("error", "Recipe not found");
        return "error/404";
    }

    //================================================================================
    // DELETE Methods
    //================================================================================

    /**
     * Removes a recipe from the system
     * @param id The ID of the recipe to delete
     * @return Redirects to the recipe list with a success message
     */
    @DeleteMapping("{id}")
    public String deleteRecipe(@PathVariable int id, Model model) {
        recipeService.deleteById(id);
        model.addAttribute("message", "Deleted recipe");
        return "redirect:/recipes";
    }






}
