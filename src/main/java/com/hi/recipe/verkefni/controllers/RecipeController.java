package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import com.hi.recipe.verkefni.services.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService){
        this.recipeService = recipeService;
    }

    @GetMapping("/recipes")
    public ResponseEntity<List<Recipe>> getFoo(@RequestParam(value="query", required = false) String query){
        // localhost:8000/recipes skilar öllum uppskriftum
        if (query == null || query.isEmpty()) {
            return ResponseEntity.ok(recipeService.findAll()); // Fetch and return all recipes
        }
        //localhost:8000/recipes?query=eggjahræra dæmi um query sem hægt er að gera líka
        return ResponseEntity.ok(recipeService.findByTitleContainingIgnoreCase(query));
    }


    /*
    @GetMapping("/recipes")
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.findAll());
    }

     */


    /*
    //Post method for creating a new reicpe
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@RequestBody Recipe recipe) {
        Recipe savedRecipe = recipeRepository.save(recipe);
        return ResponseEntity.ok(savedRecipe);
    }
     */
}
