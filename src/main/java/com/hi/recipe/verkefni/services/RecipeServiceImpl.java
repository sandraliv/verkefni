package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import com.hi.recipe.verkefni.klasar.SortType;
import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import com.hi.recipe.verkefni.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public List<Recipe> findByTagsIn(Set<RecipeTag> tags, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.findByTagsIn(tags, pageable);
        return recipes.getContent();
    }

    @Override
    public List<Recipe> findAllPaginated() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.findAllPaginated(pageable);
        return recipes.getContent();
    }

    public List<Recipe> findAllPaginated(int page, int size) {
        return List.of();
    }

    public int getTotalPages(int size) {
        Pageable pageable = PageRequest.of(0, size); // Use page 0 since we're only interested in total pages
        Page<Recipe> recipes = recipeRepository.findAllPaginated(pageable);
        return recipes.getTotalPages();
    }

    // Helper method to determine the sort order based on SortType
    private Sort getSort(SortType sortType) {
        switch (sortType) {
            case RATING:
                return Sort.by(Sort.Order.desc("averageRating"));
            case DATE:
                return Sort.by(Sort.Order.desc("dateAdded"));
            default:
                return Sort.unsorted();  // Default to no sorting if no match
        }
    }

    @Override
    public Recipe save(Recipe recipe) {
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
    public List<Recipe> findByTitleAndCategories(String title, Set<Category> categories) {
        return recipeRepository.findByTitleContainingIgnoreCaseAndCategoriesIn(title, categories);
    }


    public List<Recipe> findByCategoriesIn(Set<Category> categories, int page, int size) {
        return List.of();
    }

    @Override
    public List<Recipe> findByCategoriesIn(Set<Category> categories, int page, int size, SortType sortType) {
        Pageable pageable = getPageable(sortType, page, size);
        return recipeRepository.findByCategoriesIn(categories, pageable).getContent();
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
    public List<Recipe> findAllPaginated(int page, int size, SortType sortType) {
        Pageable pageable = getPageable(sortType, page, size);
        Page<Recipe> recipePage = recipeRepository.findAll(pageable);
        return recipePage.getContent();
    }

    @Override
    public List<Recipe> findByTagsIn(Set<RecipeTag> tags, int page, int size, SortType sortType) {
        Pageable pageable = getPageable(sortType, page, size);
        return recipeRepository.findByTagsIn(tags, pageable).getContent();
    }
    @Override
    public List<Recipe> findByTitleContainingIgnoreCase(String keyword, int page, int size, SortType sortType) {
        Pageable pageable = getPageable(sortType, page, size);
        return recipeRepository.findByTitleContainingIgnoreCase(keyword, pageable).getContent();
    }

    @Override
    public List<Recipe> findByTitleAndTags(String title, Set<RecipeTag> tags, int page, int size, SortType sortType) {
        Pageable pageable = getPageable(sortType, page, size);
        return recipeRepository.findByTitleContainingIgnoreCaseAndTagsIn(title, tags, pageable).getContent();
    }

    private Pageable getPageable(SortType sortType, int page, int size) {
        // Define a map for sorting fields for better scalability
        Map<SortType, String> sortFieldMap = Map.of(
                SortType.DATE, "dateAdded",
                SortType.RATING, "averageRating"
                // Add more SortTypes and fields as needed
        );

        String sortField = sortFieldMap.getOrDefault(sortType, "averageRating");  // Default to "averageRating" if no match found

        return PageRequest.of(page, size, Sort.by(sortField).descending());
    }




    @Override
    public List<Recipe> findByDate(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.findByDate(pageable);

        return recipes.getContent();
    }


    // Paginated recipes sorted by average rating
    @Override
    public List<Recipe> findAllByAverageRatingDesc(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Recipe> recipes = recipeRepository.findAllByAverageRatingDesc(pageable);
        return recipes.getContent();
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

    public List<Recipe> getSortedRecipes(String sort, Set<Category> categories, int page, int size) {
        return null;
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
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new NoSuchElementException("Recipe not found"));
        recipe.clearRatings();
        recipe.recalculateAverageRating();
        recipeRepository.save(recipe);
    }

    @Override
    public boolean addRecipeToFavorites(Recipe recipe, User user) {
        // Fetch the user from the repository (this will be the logged-in user)
        Optional<User> existingUserOptional = userRepository.findById(user.getId());

        if (existingUserOptional.isEmpty()) {
            return false; // Return false if the user doesn't exist
        }
        User existingUser = existingUserOptional.get();
        if (!existingUser.getFavourites().contains(recipe)) {
            existingUser.getFavourites().add(recipe);
            userRepository.save(existingUser);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeRecipeFromFavorites(Recipe recipe, User user) {
        // Check if the recipe is in the user's favorites
        if (user.getFavourites().contains(recipe)) {
            user.removeFavourite(recipe);
            recipe.getUserFavorites().remove(user);
            userRepository.save(user); // Save the updated user
            recipeRepository.save(recipe); // Save the updated recipe
            return true;
        }
        return false;
    }

}











