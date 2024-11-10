package com.hi.recipe.verkefni.repository;

import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    // Query to find Subcategory by its name and a given Category's name
    @Query("SELECT s FROM Subcategory s WHERE s.name = :subcategoryName AND s.category.name = :categoryName")
    Optional<Subcategory> findBySubNameAndCategoryName(@Param("subcategoryName") String subcategoryName, @Param("categoryName") String categoryName);

    // Query to find Subcategory by its name across all categories
    @Query("SELECT s FROM Subcategory s WHERE s.name = :subcategoryName")
    Optional<Subcategory> findByName(@Param("subcategoryName") String subcategoryName);


// Find all Subcategories by a specific Category
    @Query("SELECT s FROM Subcategory s WHERE s.category.id = :categoryId")
    List<Subcategory> findByCategoryId(Long categoryId);

    // Find all Subcategories under a specific Category by its name
    @Query("SELECT s FROM Subcategory s WHERE s.category.name = :categoryName")
    List<Subcategory> findByCategoryName(String categoryName);

    // Find all Subcategories by their name (case insensitive)
    List<Subcategory> findByNameContainingIgnoreCase(String namePattern);

    // Find recipes by subcategory name (many-to-many relationship)
    @Query("SELECT r FROM Recipe r JOIN r.subcategories s WHERE s.name = :subcategoryName")
    List<Recipe> findRecipesBySubcategoryName(@Param("subcategoryName") String subcategoryName);

    // Custom query to find Subcategory by its name and Category ID
    @Query("SELECT s FROM Subcategory s WHERE s.name = :subcategoryName AND s.category.id = :categoryId")
    Optional<Subcategory> findByNameAndCategoryId(@Param("subcategoryName") String subcategoryName, @Param("categoryId") Long categoryId);

    // Corrected query to find Recipes by Subcategory ID (many-to-many relationship)
    @Query("SELECT r FROM Recipe r JOIN r.subcategories s WHERE s.id = :subcategoryId")
    List<Recipe> findRecipesBySubcategoryId(Long subcategoryId);

    // Custom query to check if the subcategory exists and return the associated category
    @Query("SELECT s.category FROM Subcategory s WHERE s.name = :subcategoryName")
    Optional<Category> findCategoryBySubcategoryName(@Param("subcategoryName") String subcategoryName);

    Optional<Subcategory> findByNameAndCategory(String name, Category category);  // Method to find subcategory by name and category
    /**
     * Finds a subcategory by its name and associated category.
     */
    @Query("SELECT s FROM Subcategory s WHERE s.name = :name AND s.category IN :categories")
    Optional<Subcategory> findByNameAndCategoryIn(@Param("name") String name, @Param("categories") Set<Category> categories);

    // Custom query to check if a subcategory with the given name exists
    boolean existsByName(String subcategoryName);

}



