package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository)
    {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    @Override
    public Recipe save(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @Override
    public void delete(Recipe recipe) {
        recipeRepository.delete(recipe);
    }

    @Override
    public List<Recipe> findByTitleContainingIgnoreCase(String keyword) {
        System.out.println("keyword = "+keyword);
        System.out.println(recipeRepository.findByTitleContaining(keyword));
        return recipeRepository.findByTitleContaining(keyword);
    }

    @Override
    public Recipe findRecipeById(Long id){
        System.out.println("id = " + id);
        return recipeRepository.getRecipeById(id);
    }

    @Transactional
    public void resetAndReaddRecipes() {
        // Step 1: Fetch all existing recipes
        List<Recipe> existingRecipes = new ArrayList<>(recipeRepository.findAll());

        // Step 2: Delete all recipes
        recipeRepository.deleteAll();

        // Step 3: Re-add the recipes (this will generate new IDs)
        for (Recipe recipe : existingRecipes) {
            Recipe newRecipe = new Recipe(recipe.getTitle(), recipe.getDescription());
            recipeRepository.save(newRecipe);
        }
    }
}
