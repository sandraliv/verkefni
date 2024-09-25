package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Recipe;
import org.springframework.data.repository.query.Param;

import java.util.List;

//Hvaða fleiri CRUD aðferðir viljum við gera á recipes?
public interface RecipeService {
    List<Recipe> findAll();
    Recipe save(Recipe recipe);
    void delete(Recipe recipe);
    List<Recipe> findByTitleContainingIgnoreCase(String keyword);
}
