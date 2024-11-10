package com.hi.recipe.verkefni.repository;

import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find a Category by its name (derived query)
    Optional<Category> findByName(String name);
    // Query to find categories by a set of category names
    List<Category> findByNameIn(Set<String> names);

    // Query to find all categories, ensuring uniqueness
    @Query("SELECT DISTINCT c FROM Category c")
    Set<Category> findAllDistinct();


    // Find all categories associated with a recipe
    @Query("SELECT c FROM Category c JOIN c.recipes r WHERE r.id = :recipeId")
    List<Category> findCategoriesByRecipeId(Long recipeId);

    // Find all subcategories belonging to a category
    @Query("SELECT s FROM Subcategory s WHERE s.category.id = :categoryId")
    List<Subcategory> findSubcategoriesByCategoryId(Long categoryId);

    // Find categories by their name pattern (useful for search)
    List<Category> findByNameContainingIgnoreCase(String namePattern);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.subcategories WHERE c.id = :categoryId")
    Category findCategoryWithSubcategories(@Param("categoryId") Long categoryId);


}
