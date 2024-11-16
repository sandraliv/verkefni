package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import com.hi.recipe.verkefni.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;


    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    // Fetch all recipes
    @Override
    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    // Delete recipe by id
    @Override
    @Transactional
    public void deleteById(int id) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isPresent()) {
            Recipe recipe = recipeOpt.get();

            // Remove the recipe from user favorites first
            for (User user : recipe.getUserFavorites()) {
                user.getFavourites().remove(recipe);
                //System.out.println("hello"+user.getFavourites());
            }

            // Now delete the recipe
            recipeRepository.deleteById(id);
        }
    }

    @Override
    public List<Recipe> findAllPaginated() {
        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.findAllPaginated(pageable);
        System.out.println("Total elements: " + recipes.getTotalElements());  // Total number of records
        System.out.println("Total pages: " + recipes.getTotalPages());
        return recipes.getContent();
    }

    @Override
    public List<Recipe> findAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.findAllPaginated(pageable);
        return recipes.getContent();
    }

    public int getTotalPages(int size) {
        Pageable pageable = PageRequest.of(0, size); // Use page 0 since we're only interested in total pages
        Page<Recipe> recipes = recipeRepository.findAllPaginated(pageable);
        return recipes.getTotalPages();
    }

    @Override
    public Recipe save(Recipe recipe) {
        // Simply save the Recipe without modifying categories or subcategories
        return recipeRepository.save(recipe);
    }

    @Override
    public Optional<Recipe> findById(int id) {
        return recipeRepository.findById(id);
    }

    @Override
    public void delete(Recipe recipe) {
        recipeRepository.delete(recipe);
    }

    @Override
    public Optional<Recipe> findRecipeById(int id) {
        System.out.println("id = " + id);
        return recipeRepository.findById(id);
    }

    @Override
    public List<Recipe> findByTagsIn(Set<RecipeTag> tags) {
        return recipeRepository.findByTagsIn(tags);
    }

    @Override
    public List<Recipe> findByCategoriesIn(Set<Category> categories) {
        return recipeRepository.findByCategoriesIn(categories);
    }

    // Search by title (case-insensitive)
    @Override
    public List<Recipe> findByTitleContainingIgnoreCase(String keyword) {
        return recipeRepository.findByTitleContaining(keyword);
    }

    @Override
    public List<Recipe> findByTitleAndTags(String title, Set<RecipeTag> tags) {
        return recipeRepository.findByTitleContainingIgnoreCaseAndTagsIn(title, tags);
    }

    @Override
    public List<Recipe> findByDate() {

        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.findByDate(pageable);

        List<Recipe> recipeList = recipes.getContent();
        return recipeList;
    }

    // Paginated recipes sorted by average rating
    @Override
    public List<Recipe> findAllByAverageRatingDesc() {

        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.findAllByAverageRatingDesc(pageable);
        List<Recipe> recipeList = recipes.getContent();
        return recipeList;
    }

    public Set<String> getDistinctCategoryNames() {
        return Set.of();
    }

    // Helper method to format date
    @Override
    public String formatDate(LocalDateTime date) {
        return Optional.ofNullable(date)
                .map(d -> d.format(DateTimeFormatter.ofPattern("d MMMM yyyy")))
                .orElse("No Date");
    }

    @Transactional
    @Override
    public void addRating(int id, User user, int score) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));

        Map<Recipe, Integer> ratings = user.getUserRatings();
        if (ratings.containsKey(recipe)) {
            ratings.put(recipe, score);
        } else {
            ratings.put(recipe, score);
        }
        user.setUserRatings(ratings);
        userRepository.save(user);

        recipe.addRating(user, score);
        recipe.recalculateAverageRating();
        recipeRepository.save(recipe);

    }

    // Find Tags by their names (optional, if tags are needed)
    private Set<RecipeTag> findTags(List<String> tagNames) {
        Set<RecipeTag> tags = new HashSet<>();
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                RecipeTag tag = RecipeTag.valueOf(tagName);  // Assuming tags are enum values
                tags.add(tag);
            }
        }
        return tags;
    }

    @Override
    public List<Recipe> getSortedRecipes(String sort, Set<Category> categories) {
        int page = 0; // starting page
        int size = 10; // page size
        Pageable pageable = PageRequest.of(page, size);
        if (sort == null || sort.isEmpty()) {
            sort = "byDate";
        }
        if (categories != null && !categories.isEmpty()) {
            switch (sort) {
                case "highestRated":
                    return recipeRepository.findByCategoriesInOrderByAverageRatingDesc(categories, pageable).getContent();
                case "byDate":
                    return recipeRepository.findByCategoriesInOrderByDateAddedDesc(categories, pageable).getContent();
                default:
                    return recipeRepository.findByCategoriesIn(categories);
            }
        } else {
            switch (sort) {
                case "highestRated":
                    return recipeRepository.findAllByAverageRatingDesc(pageable).getContent();
                case "byDate":
                    return recipeRepository.findByDate(pageable).getContent();
                default:
                    return recipeRepository.findAllPaginated(pageable).getContent();
            }
        }
    }

    @Override
    public Set<Category> convertToCategoryEnum(Set<String> categoryStrings) {
        Set<Category> categoryEnumSet = new HashSet<>();
        if (categoryStrings != null && !categoryStrings.isEmpty()) {
            for (String category : categoryStrings) {
                try {
                    categoryEnumSet.add(Category.valueOf(category));  // Convert string to Category enum
                } catch (IllegalArgumentException e) {
                    // Handle invalid category value (log the error if needed)
                    System.err.println("Invalid category: " + category);
                }
            }
        }
        return categoryEnumSet;
    }

    @Override
    public void removeRatingFromRecipe(int recipeId) {
        // Find the recipe by ID
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));
        recipe.clearRatings();
        recipe.recalculateAverageRating();
        recipeRepository.save(recipe);
    }


}











