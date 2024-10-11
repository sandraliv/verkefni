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

    //Post new item [2] - need error handler for bad request
    @PostMapping("/newRecipe")
    public ResponseEntity<String> addANewRecipe(@RequestBody Recipe recipe) {
        recipeService.save(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body("Recipe added successfully!");
    }

    @GetMapping("/byDate")
    public ResponseEntity<List<Recipe>> getRecipesByDate(){
        return ResponseEntity.ok(recipeService.findByDate());
    }

    //Get item based on unique identifier[3] - http://localhost:8000/recipes/52
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Recipe>> getRecipeById(@PathVariable int id){
        Optional<Recipe> recipe = recipeService.findById(id);
        if (recipe.isPresent()) {
            return ResponseEntity.ok(recipe);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/addAsFav")
    public ResponseEntity<String> addRecipeToFav(@PathVariable int id, HttpSession session){
        Optional<Recipe> or = recipeService.findById(id);
        Recipe recipe;
        if(or.isPresent()){
            recipe = or.get();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Problem adding facvourite");
        }
        User user = (User)session.getAttribute("user");
        if(user != null){
            user.setFavourites(recipe);
            userService.save(user);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Favourite added to logged in user");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not update user");
        }
    }
    //Get item[1] - http://localhost:8000/recipes
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes(@RequestParam(value="query", required = false) String query, @RequestParam(value="tags", required = false) Set<RecipeTag> tags){
        if ((query == null || query.isEmpty()) && tags == null ) {
            return ResponseEntity.ok(recipeService.findAllPaginated()); // Fetch and return all recipes
        } else if (tags == null || tags.isEmpty()) {
            return ResponseEntity.ok(recipeService.findByTitleContainingIgnoreCase(query));
        } else if (query == null || query.isEmpty()) {
            return  ResponseEntity.ok(recipeService.findByTagsIn(tags));
        }
        return ResponseEntity.ok(recipeService.findByTitleAndTags(query, tags));
    }

    //PATCH add tag to recipe[1] - http://localhost:8000/recipes/52/addTag?tag=LOW_CARB
    @PatchMapping("/{id}/addTag")
    public ResponseEntity<String> addTagToRecipe(@PathVariable int id, @RequestParam RecipeTag tag) {
        Optional<Recipe> optionalRecipe = recipeService.findById(id);
        if (optionalRecipe.isPresent()) {
            Recipe recipe = optionalRecipe.get();
            recipe.getTags().add(tag);
            recipeService.save(recipe);
            return ResponseEntity.ok("Tag added successfully to the recipe.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found.");
        }
    }
}
