package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    //RecipeRepository connects to the recipe table because it is tied to the Recipy entity, which is mapped to that table.
    //RecipeRepository in your controller interacts with the recipe table through the repository methods provided by JpaRepository.
    private final RecipeRepository recipeRepository;

    //Here we are injecting the RecipeRepo dependency into the controller, which allows the controller to interact with the database through the repository.
    public RecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    //GetMethod for returning all recipes
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeRepository.findAll());
    }

    //Post method for creating a new reicpe
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        Recipe savedRecipe = recipeRepository.save(recipe);
        return ResponseEntity.ok(savedRecipe);
    }
}
