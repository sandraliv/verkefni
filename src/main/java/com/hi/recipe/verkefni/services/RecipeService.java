package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Recipe;
import org.springframework.data.repository.query.Param;

import java.util.List;

//Hvaða fleiri CRUD aðferðir viljum við gera á recipes?
public interface RecipeService {
    List<Recipe> findAll();
    Recipe save(Recipe recipe);
    Recipe findRecipeById(Long id);
    void delete(Recipe recipe);
    void resetAndReaddRecipes();
    List<Recipe> findByTitleContainingIgnoreCase(String keyword);
}
