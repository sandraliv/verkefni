package com.hi.recipe.verkefni.repository;


import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    @Query("SELECT r FROM Recipe r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Recipe> findByTitleContaining(String keyword);

    @EntityGraph(attributePaths = {"tags", "categories", "ingredients"})
    @NonNull
    List<Recipe> findAll();

    Page<Recipe> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    // Paginated method to search by title and tags
    Page<Recipe> findByTitleContainingIgnoreCaseAndTagsIn(String title, Set<RecipeTag> tags, Pageable pageable);

    @EntityGraph(attributePaths = {"tags", "categories", "ingredients"})
    Page<Recipe> findByTagsIn(Collection<RecipeTag> tags, Pageable pageable);

    @Query("SELECT r FROM Recipe r ORDER BY dateAdded DESC")
    Page<Recipe> findByDate(Pageable pageable);

    @Query("SELECT r FROM Recipe r")
    @EntityGraph(attributePaths = { "tags", "categories"})
    Page<Recipe> findAllPaginated(Pageable pageable);

    @NonNull
    Optional<Recipe> findById(Integer id);

    @Query("SELECT r FROM Recipe r ORDER BY r.averageRating DESC")
    Page<Recipe> findAllByAverageRatingDesc(Pageable pageable);

    @Modifying
    @Query(value = "DELETE FROM user_recipes WHERE recipe_id = :recipeId", nativeQuery = true)
    void deleteUserRecipeRelations(@Param("recipeId") int recipeId);

    @EntityGraph(attributePaths = {"ingredients", "tags", "categories"})
    Page<Recipe> findByCategoriesIn(Collection<Category> categories, Pageable pageable);


    // Retrieve recipes by categories, ordered by average rating
    @Query("SELECT r FROM Recipe r JOIN r.categories c WHERE c IN :categories ORDER BY r.averageRating DESC")
    Page<Recipe> findByCategoriesInOrderByAverageRatingDesc(@Param("categories") Set<Category> categories, Pageable pageable);

    @Query("SELECT r FROM Recipe r JOIN r.categories c WHERE c IN :categories ORDER BY r.dateAdded DESC")
    Page<Recipe> findByCategoriesInOrderByDateAddedDesc(@Param("categories") Collection<Category> categories, Pageable pageable);

    List<Recipe> findByTitleContainingIgnoreCaseAndTagsIn(String title, Collection<RecipeTag> tags);

    List<Recipe> findByTitleContainingIgnoreCaseAndCategoriesIn(String title, Set<Category> categories);

}




