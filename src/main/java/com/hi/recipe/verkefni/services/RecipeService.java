package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

//Hvaða fleiri CRUD aðferðir viljum við gera á recipes?
public interface RecipeService {
    List<Recipe> findAll();
    List<Recipe> findAllPaginated();
    Recipe save(Recipe recipe);
    Optional<Recipe> findById(int id);
    void delete(Recipe recipe);
    List<Recipe> findByTagsIn(Collection<RecipeTag> tags);
    List<Recipe> findByTitleContainingIgnoreCase(String keyword);
    List<Recipe> findByTitleAndTags(String title, Collection<RecipeTag> tags);
    List<Recipe> findByDate();
}
