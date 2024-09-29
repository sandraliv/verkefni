package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Recipe;

import java.util.List;
import java.util.Optional;

//Hvaða fleiri CRUD aðferðir viljum við gera á recipes?
public interface RecipeService {
    List<Recipe> findAll();
    Recipe save(Recipe recipe);
    Optional<Recipe> findById(int id);
    void delete(Recipe recipe);
    List<Recipe> findByTitleContainingIgnoreCase(String keyword);
}
