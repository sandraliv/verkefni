package com.hi.recipe.verkefni.repository;

import com.hi.recipe.verkefni.klasar.UserRecipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRecipeRepository extends JpaRepository<UserRecipe, Integer> {
    Page<UserRecipe> findByUserId(int userId, Pageable pageable);
    // Optional: Find by recipe ID and user ID to ensure the user is authorized to view/edit the recipe
    UserRecipe findByIdAndUserId(int id, int userId);
}
