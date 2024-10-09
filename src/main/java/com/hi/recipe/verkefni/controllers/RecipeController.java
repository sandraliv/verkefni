package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import com.hi.recipe.verkefni.services.RecipeService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService){
        this.recipeService = recipeService;
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
        System.out.println("/{id} = "+id);
        Optional<Recipe> recipe = recipeService.findById(id);
        System.out.println("Recipe = "+recipe);
        if (recipe.isPresent()) {
            return ResponseEntity.ok(recipe);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //Get item[1] - http://localhost:8000/recipes
    @GetMapping
    public ResponseEntity<List<Recipe>> getFoo(@RequestParam(value="query", required = false) String query, @RequestParam(value="tags", required = false) Set<RecipeTag> tags){
        if ((query == null || query.isEmpty()) && tags == null ) {
            System.out.println("er að prenta allar");
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
    /* Úrelt sjá PostMapping aðferð fyrir neðan.
    @GetMapping("/newRecipe")
    public void addANewRecipe(@RequestParam(value="query", required = false) String query){
        Map<String, String> oatmeal = new HashMap<>();
        oatmeal.put("Oats", "200g");
        oatmeal.put("Oatmilk", "300ml");
        oatmeal.put("Cinnmon", "2 tbsp");
        oatmeal.put("Sugar", "50g");
        Collection<RecipeTag> tags = new HashSet<>();
        tags.add(RecipeTag.VEGAN);
        tags.add(RecipeTag.GLUTEN_FREE);
        Recipe r = new Recipe("Oatmeal", "Breakfast oatmeal",  oatmeal, tags);
        recipeService.save(r);
    }
     */

}
