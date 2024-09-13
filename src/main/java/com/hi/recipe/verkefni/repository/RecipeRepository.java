package com.hi.recipe.verkefni.repository;

import com.hi.recipe.verkefni.klasar.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, String> {
    
}
