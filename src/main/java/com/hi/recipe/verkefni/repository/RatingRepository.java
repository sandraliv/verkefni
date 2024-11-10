package com.hi.recipe.verkefni.repository;

import com.hi.recipe.verkefni.klasar.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    List<Rating> findByRecipeId(int recipeId);




}
