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
            for (User user : recipe.getUserList()) {
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
        List<Recipe> recipeList = recipes.getContent();
        return recipeList;
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

    @Override
    public List<Recipe> findByTitleAndCategories(String query, Set<Category> categories) {
        return recipeRepository.findByTitleContainingIgnoreCaseAndCategoriesIn(query, categories);
    }

    @Override
    public List<Recipe> findByTagsInAndCategoriesIn(Set<RecipeTag> tags, Set<Category> categories) {
        return recipeRepository.findByTagsInAndCategoriesIn(tags, categories);
    }

    @Override
    public List<Recipe> findByTitleAndTagsAndCategories(String query, Set<RecipeTag> tags, Set<Category> categories) {
        return recipeRepository.findByTitleContainingIgnoreCaseAndTagsInAndCategoriesIn(query, tags, categories);
    }


    // Search by title (case-insensitive)
    @Override
    public List<Recipe> findByTitleContainingIgnoreCase(String keyword) {
        System.out.println(keyword);
        System.out.println(recipeRepository.findByTitleContaining(keyword));
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
    public void addTempRatingToRecipe(int recipeId, int score) {
        // Find the recipe by ID
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));

        // Add the new rating to the recipe
        recipe.addTempRating(score); // This will add the score and update the average

        // Save the recipe (this will also save the ratings in the temp_recipe_ratings table)
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
    public List<Recipe> filterRecipes(String query, Set<RecipeTag> tags) {
        if ((query == null || query.isEmpty()) && (tags == null || tags.isEmpty())) {
            // If no filter is applied, return all recipes (paginated if necessary)
            return findAllPaginated(); // Assuming this method exists for paginated results
        } else if (tags == null || tags.isEmpty()) {
            // If only query is provided, filter by title
            return findByTitleContainingIgnoreCase(query);
        } else if (query == null || query.isEmpty()) {
            // If only tags are provided, filter by tags
            return findByTagsIn(tags);
        } else {
            // If both query and tags are provided, filter by both
            return findByTitleAndTags(query, tags);
        }
    }

    @Override
    public List<Recipe> getSortedRecipes(String sort, Set<Category> categories) {
        int page = 0; // starting page
        int size = 10; // page size

        Pageable pageable = PageRequest.of(page, size);

        // Default sorting by date if 'sort' is null or invalid
        if (sort == null || sort.isEmpty()) {
            sort = "byDate";
        }
        // If categories are provided, return filtered and sorted results
        if (categories != null && !categories.isEmpty()) {
            switch (sort) {
                case "highestRated":
                    // Sort by rating and apply categories filter
                    return recipeRepository.findByCategoriesInOrderByAverageRatingDesc(categories, pageable).getContent();
                case "byDate":
                    // Sort by date and apply categories filter
                    return recipeRepository.findByCategoriesInOrderByDateAddedDesc(categories, pageable).getContent();
                default:
                    // Default unsorted with category filter
                    return recipeRepository.findByCategoriesIn(categories);
            }
        } else {
            // If no categories, return all recipes sorted by the selected option
            switch (sort) {
                case "highestRated":
                    // Sort by rating, no category filter
                    return recipeRepository.findAllByAverageRatingDesc(pageable).getContent();
                case "byDate":
                    // Sort by date, no category filter
                    return recipeRepository.findByDate(pageable).getContent();
                default:
                    // Default unsorted, no category filter
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
    public Set<RecipeTag> convertToRecipeTagEnum(Set<String> tagStrings) {
        Set<RecipeTag> tagEnumSet = new HashSet<>();
        if (tagStrings != null && !tagStrings.isEmpty()) {
            for (String tag : tagStrings) {
                try {
                    tagEnumSet.add(RecipeTag.valueOf(tag));  // Convert string to RecipeTag enum
                } catch (IllegalArgumentException e) {
                    // Handle invalid tag value (optional: log the error)
                    System.err.println("Invalid tag: " + tag);
                }
            }
        }
        return tagEnumSet;
    }





}











