package com.hi.recipe.verkefni.repository;


import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Recipe> findByTitleContaining(String keyword);

    List<Recipe> findByTagsIn(Collection<RecipeTag> tags);

    @Query("SELECT r FROM Recipe r JOIN r.categories c WHERE c IN :categories")
    List<Recipe> findByCategoryIn(@Param("categories") Set<Category> categories);


    List<Recipe> findByTitleContainingIgnoreCaseAndTagsIn(String title, Collection<RecipeTag> tags);

    @Query("SELECT r FROM Recipe r ORDER BY dateAdded DESC")
    Page<Recipe> findByDate(Pageable pageable);

    @Query("SELECT r FROM Recipe r")
    @EntityGraph(attributePaths = {"ingredients", "tags"})
    Page<Recipe> findAllPaginated(Pageable pageable);

    // Method to find a recipe by its ID
    Optional<Recipe> findById(Integer id);

    @Query("SELECT r FROM Recipe r ORDER BY r.averageRating DESC")
    Page<Recipe> findAllByAverageRatingDesc(Pageable pageable);

}



