package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.*;
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
import java.util.stream.Collectors;

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
    public List<Recipe> findByTagsIn(Set<RecipeTag> tags) {
        return recipeRepository.findByTagsIn(tags);
    }

    @Override
    public List<Recipe> findByCategoryIn(Set<Category> categories) {
        return recipeRepository.findByCategoryIn(categories);
    }





    public Optional<Recipe> findRecipeById(int id) {
        System.out.println("id = " + id);
        return recipeRepository.findById(id);
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
        int size = 2;

        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.findByDate(pageable);

        List<Recipe> recipeList = recipes.getContent();
        return recipeList;
    }

    // Paginated recipes sorted by average rating
    @Override
    public List<Recipe> findAllByAverageRatingDesc() {

        int page = 0;
        int size = 2;

        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.findAllByAverageRatingDesc(pageable);
        List<Recipe> recipeList = recipes.getContent();
        return recipeList;
    }


    @Override
    public <T> List<String> convertEntitiesToList(Collection<T> entities) {
        return entities.stream()
                .map(entity -> entity instanceof Enum ? ((Enum<?>) entity).name() : entity.toString())
                .collect(Collectors.toList());
    }




    // Helper method to format date
    @Override
    public String formatDate(LocalDateTime date) {
        return Optional.ofNullable(date)
                .map(d -> d.format(DateTimeFormatter.ofPattern("d MMM yyyy")))
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


}











