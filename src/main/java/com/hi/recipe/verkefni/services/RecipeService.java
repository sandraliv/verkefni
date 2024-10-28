package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import com.hi.recipe.verkefni.klasar.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RecipeService {
    List<Recipe> findAll();
    List<Recipe> findAllPaginated();
    Recipe save(Recipe recipe);
    Optional<Recipe> findById(int id);
    void delete(Recipe recipe);
    void deleteById(int id);
    List<Recipe> findByTagsIn(Set<RecipeTag> tags);
    List<Recipe> findByTitleContainingIgnoreCase(String keyword);
    List<Recipe> findByTitleAndTags(String title, Set<RecipeTag> tags);
    List<Recipe> findByDate();
}
