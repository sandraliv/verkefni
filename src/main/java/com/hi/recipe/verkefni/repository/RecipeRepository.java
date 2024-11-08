package com.hi.recipe.verkefni.repository;


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

    List<Recipe> findByTitleContainingIgnoreCaseAndTagsIn(String title, Collection<RecipeTag> tags);

    @Query("SELECT r FROM Recipe r ORDER BY dateAdded DESC")
    Page<Recipe> findByDate(Pageable pageable);

    @Query("SELECT r FROM Recipe r")
    @EntityGraph(attributePaths = {"ingredients", "tags"})
    Page<Recipe> findAllPaginated(Pageable pageable);

    // Method to find a recipe by its ID
    Optional<Recipe> findById(Integer id);

    @Query("SELECT r FROM Recipe r ORDER BY r.averageRating DESC")
    List<Recipe> findAllByAverageRatingDesc();

    @Query("SELECT r FROM Recipe r " +
            "JOIN r.categories c " +
            "LEFT JOIN r.subcategories s " +
            "WHERE c.name = :categoryName AND s.name = :subcategoryName")
    List<Recipe> findByCategoryNameIncludingSubcategories(@Param("categoryName") String categoryName, @Param("subcategoryName") String subcategoryName);


    // Query by category and subcategory IDs
    @Query("SELECT r FROM Recipe r " +
            "JOIN r.categories c " +
            "JOIN r.subcategories s " +
            "WHERE c.id = :categoryId AND s.id = :subcategoryId")
    List<Recipe> findByCategoryAndSubcategory(@Param("categoryId") Long categoryId, @Param("subcategoryId") Long subcategoryId);

    // List search for all recipes by category ID (Many-to-Many relationship)
    List<Recipe> findByCategoriesId(Long categoryId);

    // Derived Query: Find recipes by subcategory name
    @Query("SELECT r FROM Recipe r " +
            "JOIN r.subcategories s " +
            "WHERE s.name = :subcategoryName")
    List<Recipe> findBySubcategoryName(@Param("subcategoryName") String subcategoryName);

    // Custom query to find recipes by category name (directly associated categories only)
    @Query("SELECT r FROM Recipe r " +
            "JOIN r.categories c " +
            "WHERE c.name = :categoryName")
    List<Recipe> findByCategoryName(@Param("categoryName") String categoryName);

    // Custom query to find recipes by subcategory ID (directly associated subcategories only)
    @Query("SELECT r FROM Recipe r " +
            "JOIN r.subcategories s " +
            "WHERE s.id = :subcategoryId")
    List<Recipe> findBySubcategoryId(@Param("subcategoryId") Long subcategoryId);

    @Query("SELECT s.name FROM Subcategory s JOIN s.recipes r WHERE r = :recipe")
    Set<String> findSubcategoryNamesByRecipe(@Param("recipe") Recipe recipe);

    Page<Recipe> findAll(Pageable pageable);
}



