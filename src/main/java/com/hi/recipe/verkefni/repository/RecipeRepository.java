package com.hi.recipe.verkefni.repository;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Recipe> findByTitleContaining(String keyword);

    List<Recipe> findByTagsIn(Collection<RecipeTag> tags);

    List<Recipe> findByTitleContainingIgnoreCaseAndTagsIn(String title, Collection<RecipeTag> tags);

    @Query("SELECT r FROM Recipe r ORDER BY dateAdded DESC")
    Page<Recipe> findByDate(Pageable pageable);

    @Query("SELECT r FROM Recipe r")
    @EntityGraph(attributePaths = {"ingredients", "tags"})
    Page<Recipe> findAllPaginated(Pageable pageable);
}
