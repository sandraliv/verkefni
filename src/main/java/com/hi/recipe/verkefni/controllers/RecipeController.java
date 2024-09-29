package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import com.hi.recipe.verkefni.services.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService){
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes(@RequestParam(value="query", required = false) String query){
        // localhost:8000/recipes skilar öllum uppskriftum
        System.out.println("GET / ");
        if (query == null || query.isEmpty()) {
            System.out.println(recipeService.findAll());
            return ResponseEntity.ok(recipeService.findAll()); // Fetch and return all recipes
        }
        //localhost:8000/recipes?query=eggjahræra dæmi um query sem hægt er að gera líka
        return ResponseEntity.ok(recipeService.findByTitleContainingIgnoreCase(query));
    }
    //@PostMapping("/new")
    @GetMapping("/new")
    public void addNewRecipe(@RequestParam(value="query", required = false) String query){
        Recipe r = new Recipe("Samloka", "Samloka með osta og skinku");
        recipeService.save(r);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id){
        System.out.println("/{id} = "+id);
        Recipe recipe = recipeService.findRecipeById(id);
        System.out.println("Recipe = "+recipe);
        if (recipe != null) {
            return ResponseEntity.ok(recipe);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
/* 
    @GetMapping("/reset")
    public ResponseEntity<String> resetAndReaddRecipes() {
        recipeService.resetAndReaddRecipes();
        return ResponseEntity.ok("Recipes have been reset and re-added with new IDs");
    }

    
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
