package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.*;
import com.hi.recipe.verkefni.repository.CategoryRepository;
import com.hi.recipe.verkefni.repository.RatingRepository;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import com.hi.recipe.verkefni.repository.SubcategoryRepository;
import com.hi.recipe.verkefni.utils.CategoryUtils;
import com.hi.recipe.verkefni.utils.SubcategoryUtils;
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
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final RecipeRepository recipeRepository;
    private final RatingRepository ratingRepository;


    @Autowired
    public RecipeServiceImpl(RecipeRepository recipeRepository, CategoryRepository categoryRepository,
                             SubcategoryRepository subcategoryRepository, RatingRepository ratingRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.ratingRepository = ratingRepository;


    }

   /* @Override
    public void updateAverageRating(Recipe recipe) {
        Set<Integer> ratings = recipe.getTempRatings();
        double average = ratings.stream().mapToInt(Rating::getScore).average().orElse(0);
        recipe.setAverageRating(BigDecimal.valueOf(average));
        recipeRepository.save(recipe);
    }*/
    // Fetch all recipes
    @Override
    public List<Recipe> findAll() {
        return recipeRepository.findAll();
    }
    // Delete recipe by id
    @Override
    public void deleteById(int id) {
        recipeRepository.deleteById(id);
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

    public Rating save(Rating rating) {
        return ratingRepository.save(rating);
    }

    @Override

    public Recipe save(Recipe recipe) {
        // Step 1: Process Categories using utility class
        Set<String> categoryNames = CategoryUtils.extractCategoryNames(recipe.getCategories());  // Extract category names as Set<String>
        Set<Category> categories = new HashSet<>();  // Set to hold the categories

        for (String categoryName : categoryNames) {
            // Use the utility method to find or create each category
            Category category = CategoryUtils.findOrCreateCategories(categoryName, categoryRepository);
            categories.add(category);  // Add the found/created category to the set
        }

        recipe.setCategories(categories);  // Associate categories with the recipe

        // Step 2: Handle Subcategories using utility class
        Set<Subcategory> subcategories = new HashSet<>();
        if (recipe.getSubcategories() != null && !recipe.getSubcategories().isEmpty()) {
            for (Subcategory subcategory : recipe.getSubcategories()) {
                String subcategoryName = subcategory.getName();  // Extract name from Subcategory object

                // Get or create the subcategory using the utility method that returns Optional
                Optional<Subcategory> existingSubcategory = SubcategoryUtils.getOrCreateSubcategory(subcategoryName, categories, subcategoryRepository);

                // If the Optional contains a subcategory, add it to the set
                existingSubcategory.ifPresent(subcategories::add);
            }
        }

        recipe.setSubcategories(subcategories);  // Associate subcategories with the recipe

        // Step 3: Save the Recipe to the repository
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

    public List<Rating> findByRecipeId(int recipeId) {
        return ratingRepository.findByRecipeId(recipeId);
    }
    // Paginated recipes sorted by average rating
    @Override
    public List<Recipe> findAllByAverageRatingDesc() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Recipe> recipes = recipeRepository.findAllByAverageRatingDesc();
        return paginateList(recipes, pageable);
    }

    // Helper method for pagination logic
    private List<Recipe> paginateList(List<Recipe> recipes, Pageable pageable) {
        int start = Math.min((int) pageable.getOffset(), recipes.size());
        int end = Math.min(start + pageable.getPageSize(), recipes.size());
        return recipes.subList(start, end);
    }


    @Override
    public List<String> convertTagsToList(Recipe recipe) {
        return convertEntitiesToList(recipe.getTags());
    }
    @Override
    public List<String> convertCategoriesToList(Recipe recipe) {
        return convertEntitiesToList(recipe.getCategories());
    }
    @Override
    public List<String> convertSubcategoriesToList(Recipe recipe) {
        return convertEntitiesToList(recipe.getSubcategories());
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


    /*@Transactional
    public void addRatingToRecipe(int recipeId, int score) {
        // Find the recipe by ID
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));

        // Create the Rating and associate it with the recipe
        Rating rating = new Rating();
        rating.setScore(score);
        rating.setRecipe(recipe);
        recipe.setDateAdded(LocalDateTime.now());

        // Set the rating's ID to be the same as the recipe's ID
        rating.setId((int) recipeId);  // Set the Rating ID to match the Recipe ID

        // Save the rating (this will insert a row in the ratings table)
        ratingRepository.save(rating);

        // Add the rating to the recipe
        recipe.addRating(rating);



        // Save the updated recipe (this will persist both the recipe and the rating)
        recipeRepository.save(recipe);
    }*/
    @Transactional
    public void addTempRatingToRecipe(int recipeId, int score) {
        // Find the recipe by ID
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));

        // Add the new rating to the recipe
        recipe.addTempRating(score); // This will add the score and update the average

        // Save the recipe (this will also save the ratings in the temp_recipe_ratings table)
        recipeRepository.save(recipe);
    }

    @Override
    public List<Recipe> findByCategory(String categoryName) {
        return recipeRepository.findByCategoryName(categoryName);
    }

    @Override
    public List<Recipe> findBySubcategory(String subcategoryName) {
        return recipeRepository.findBySubcategoryName(subcategoryName);
    }

    @Override
    public List<Recipe> findByCategoriesId(Long categoryId) {
        return recipeRepository.findByCategoriesId(categoryId);
    }

    @Override
    public List<Recipe> findBySubcategoryId(Long subcategoryId) {
        return recipeRepository.findBySubcategoryId(subcategoryId);
    }

    // Find recipes by category and subcategory using IDs
    @Override
    public List<Recipe> findByCategoryAndSubcategory(Long categoryId, Long subcategoryId) {
        return recipeRepository.findByCategoryAndSubcategory(categoryId, subcategoryId);
    }

    // Find recipes by category name and subcategory name
    @Override
    public List<Recipe> findByCategoryNameIncludingSubcategories(String categoryName, String subcategoryName) {
        return recipeRepository.findByCategoryNameIncludingSubcategories(categoryName, subcategoryName);

    }
    // Find Categories by their names
    private Set<Category> findCategories(List<String> categoryNames) {
        Set<Category> categories = new HashSet<>();

        // If categoryNames is provided (not null and not empty), try to find categories
        if (categoryNames != null && !categoryNames.isEmpty()) {
            for (String categoryName : categoryNames) {
                // Find the category entity by its name. If not found, just skip it.
                Optional<Category> category = categoryRepository.findByName(categoryName);
                category.ifPresent(categories::add);
            }
        }
        return categories; // Return the set (can be empty if no categories were provided or found)
    }

    private Set<Subcategory> findSubcategories(List<String> subcategoryNames) {
        Set<Subcategory> subcategories = new HashSet<>();

        // If subcategoryNames is provided (not null and not empty), try to find subcategories
        if (subcategoryNames != null && !subcategoryNames.isEmpty()) {
            for (String subcategoryName : subcategoryNames) {
                // Find the subcategory entity by its name. If not found, just skip it.
                Optional<Subcategory> subcategory = subcategoryRepository.findByName(subcategoryName);
                subcategory.ifPresent(subcategories::add);
            }
        }

        return subcategories; // Return the set (can be empty if no subcategories were provided or found)
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











