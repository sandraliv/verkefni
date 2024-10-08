package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import com.hi.recipe.verkefni.services.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService){
        this.recipeService = recipeService;
    }

    @GetMapping("/new")
    public void addNewRecipe(@RequestParam(value="query", required = false) String query){
        Map<String, String> pancakeIngredients = new HashMap<>();
        pancakeIngredients.put("Flour", "200g");
        pancakeIngredients.put("Milk", "300ml");
        pancakeIngredients.put("Eggs", "2");
        pancakeIngredients.put("Sugar", "50g");
        Set<RecipeTag> tags = new HashSet<>();
        tags.add(RecipeTag.VEGAN);
        tags.add(RecipeTag.GLUTEN_FREE);
        Recipe r = new Recipe("Samloka", "Samloka með osta og skinku",  pancakeIngredients, tags);
        Recipe b = recipeService.save(r);
        System.out.println(b.getDescription());
    }

    @GetMapping("/newRecipe")
    public void addANewRecipe(@RequestParam(value="query", required = false) String query){
        Map<String, String> oatmeal = new HashMap<>();
        oatmeal.put("Oats", "200g");
        oatmeal.put("Oatmilk", "300ml");
        oatmeal.put("Cinnmon", "2 tbsp");
        oatmeal.put("Sugar", "50g");
        Collection<RecipeTag> tags = new HashSet<>();
        tags.add(RecipeTag.KETO);
        Recipe r = new Recipe("Oatmeal", "Breakfast oatmeal",  oatmeal, tags);
        recipeService.save(r);
    }

    @GetMapping("recipes/{id}")
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

    //ÞETTA HÉR ER Í RUGLINU
    //HVERNIG GET ÉG BÚIÐ TIL ÖLL CASE-IN FJÖGUR, QUERY+TAGS/TAGS/QUERY/NONE
    @GetMapping("/recipes")
    public ResponseEntity<List<Recipe>> getFoo(@RequestParam(value="query", required = false) String query, @RequestParam(value="tags", required = false) Collection<RecipeTag> tags){
        // localhost:8000/recipes skilar öllum uppskriftum
        if ((query == null || query.isEmpty()) && tags == null ) {
            System.out.println("er að prenta allar");
            return ResponseEntity.ok(recipeService.findAll()); // Fetch and return all recipes
        } else if (tags == null || tags.isEmpty()) {
            return ResponseEntity.ok(recipeService.findByTitleContainingIgnoreCase(query));

        } else if (query == null || query.isEmpty()) {
            return  ResponseEntity.ok(recipeService.findByTagsIn(tags));
        }
        return ResponseEntity.ok(recipeService.findByTitleAndTags(query, tags));
    }

}
