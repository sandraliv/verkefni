package com.hi.recipe.verkefni.repository;

import com.hi.recipe.verkefni.klasar.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, String> {
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Recipe> findByTitleContaining(String keyword);

    @Query("Select r FROM Recipe r")
    List<Recipe> findAll();

    @Query("SELECT r FROM Recipe r WHERE r.id = id")
    Recipe getRecipeById(Long id);
}
