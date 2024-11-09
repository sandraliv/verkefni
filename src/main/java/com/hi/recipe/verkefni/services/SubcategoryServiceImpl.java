package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.Subcategory;
import com.hi.recipe.verkefni.repository.CategoryRepository;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import com.hi.recipe.verkefni.repository.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.hi.recipe.verkefni.utils.CategoryUtils.normalizeCategoryName;

@Service
public class SubcategoryServiceImpl implements SubcategoryService {
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeService recipeService;


    @Autowired
    public SubcategoryServiceImpl(CategoryRepository categoryRepository, RecipeRepository recipeRepository, SubcategoryRepository subcategoryRepository, RecipeService recipeService) {
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.recipeRepository = recipeRepository;
        this.recipeService = recipeService;
    }

    @Override
    public Optional<Subcategory> getSubcategoryByName(String subcategoryName) {
        return subcategoryRepository.findByName(subcategoryName);
    }
    @Override
    public List<Recipe> getRecipesBySubcategoryId(Long subcategoryId) {
        return subcategoryRepository.findRecipesBySubcategoryId(subcategoryId);

    }
    // Get Subcategories by Category ID
    @Override
    public List<Subcategory> getSubcategoriesByCategoryId(Long categoryId) {
        return subcategoryRepository.findByCategoryId(categoryId);
    }
    @Override
    public List<Recipe> getRecipesBySubcategoryName(String subcategoryName) {
        return subcategoryRepository.findRecipesBySubcategoryName(subcategoryName);
    }
    @Override
    public List<Subcategory> getSubcategoriesByCategoryName(String categoryName) {
        // Normalize the category name first (e.g., "baking" -> "Baking")
        String normalizedCategoryName = normalizeCategoryName(categoryName);

        // Find the category by the normalized name
        Optional<Category> categoryOptional = categoryRepository.findByName(normalizedCategoryName);

        // If the category is found by the normalized name
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            // Return subcategories by categoryId
            return subcategoryRepository.findByCategoryId(category.getId());
        } else {
            // If the category wasn't found by the normalized name, search by the original (unnormalized) name
            return findSubcategoriesByOriginalCategoryName(categoryName);
        }
    }
    @Override
    public List<Subcategory> findSubcategoriesByOriginalCategoryName(String categoryName) {
        // Find the category by the unnormalized (exact) name
        Optional<Category> categoryOptional = categoryRepository.findByName(categoryName);

        // If the category is found, return the associated subcategories by categoryId
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            return subcategoryRepository.findByCategoryId(category.getId());  // Use categoryId directly
        } else {
            // If not found, return an empty list
            return Collections.emptyList();
        }
    }



    @Override
    public Optional<Subcategory> getSubcategoryByNameAndCategoryId(String subcategoryName, Long categoryId) {
        return subcategoryRepository.findByNameAndCategoryId(subcategoryName, categoryId);
    }
    @Override
    public Optional<Category> getCategoryBySubcategoryName(String subcategoryName) {
        // Find the subcategory by name
        Optional<Subcategory> subcategoryOpt = subcategoryRepository.findByName(subcategoryName);

        if (subcategoryOpt.isPresent()) {
            // Get the Category associated with this subcategory
            Subcategory subcategory = subcategoryOpt.get();
            Category category = subcategory.getCategory();  // Assuming a @ManyToOne or @OneToMany relationship
            return Optional.of(category);
        }
        return Optional.empty();
    }
    @Override
    public boolean checkSubcategoryExistence(String subcategoryName) {
        return subcategoryRepository.existsByName(subcategoryName);
    }


    /**
     * Find subcategory by its name and associated category.
     */
    @Override
    public Optional<Subcategory> findSubcategoryByNameAndCategory(String subcategoryName, Set<Category> categories) {
        return subcategoryRepository.findByNameAndCategoryIn(subcategoryName, categories);
    }

    /**
     * Save a new subcategory.
     */
    @Override
    public Subcategory save(Subcategory subcategory) {
        return subcategoryRepository.save(subcategory);
    }


    @Override
    public Subcategory createSubcategoryIfNotExists(String subcategoryName, Category category) {
        Optional<Subcategory> existingSubcategory = getSubcategoryByNameAndCategoryId(subcategoryName, category.getId());
        if (existingSubcategory.isEmpty()) {
            Subcategory newSubcategory = new Subcategory();
            newSubcategory.setName(subcategoryName);
            newSubcategory.setCategory(category);
            return subcategoryRepository.save(newSubcategory);
        }
        return existingSubcategory.get(); // Return the existing subcategory if found
    }


    @Override
    public Subcategory createSubcategory(Subcategory subcategory) {
        return subcategoryRepository.save(subcategory);
    }

    // Update an existing Subcategory
    @Override
    public Subcategory updateSubcategory(Long id, Subcategory subcategory) {
        // First check if subcategory exists
        Subcategory existingSubcategory = subcategoryRepository.findById(id).orElse(null);
        if (existingSubcategory != null) {
            existingSubcategory.setName(subcategory.getName());
            existingSubcategory.setCategory(subcategory.getCategory()); // Update Category if necessary
            return subcategoryRepository.save(existingSubcategory);
        }
        return null; // Subcategory not found
    }

    @Override
    public Optional<Subcategory> findBySubcategoryId(Long subcategoryId) {
        return subcategoryRepository.findById(subcategoryId);
    }

    @Override
    public boolean deleteSubcategory(Long id) {
        if (subcategoryRepository.existsById(id)) {
            subcategoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
    @Override
    public Optional<Category> checkSubcategoryExistenceAndGetCategory(String subcategoryName) {
        Optional<Category> category = subcategoryRepository.findCategoryBySubcategoryName(subcategoryName);
        if (category.isPresent()) {
            return category; // Subcategory exists, return the category it belongs to
        }
        return Optional.empty(); // Subcategory does not exist
    }

    // Search Subcategories by Name Pattern
    @Override
    public List<Subcategory> searchSubcategoriesByName(String namePattern) {
        return subcategoryRepository.findByNameContainingIgnoreCase(namePattern);
    }
    /**
     * Check if a Subcategory name exists within a given Category by Category Name
     */
    @Override
    public boolean doesSubcategoryExistInCategory(String subcategoryName, String categoryName) {
        // Normalize both subcategory and category names
        subcategoryName = normalizeCategoryName(subcategoryName);
        categoryName = normalizeCategoryName(categoryName);

        // Retrieve the category by name
        Optional<Category> categoryOptional = categoryRepository.findByName(categoryName);

        if (!categoryOptional.isPresent()) {
            // If the category doesn't exist, return false
            return false;
        }

        Category category = categoryOptional.get();

        // Check if the subcategory exists in the category
        Optional<Subcategory> subcategoryOptional = subcategoryRepository.findByNameAndCategory(subcategoryName, category);

        return subcategoryOptional.isPresent();  // Return true if subcategory exists, otherwise false
    }


    /**
     * Check if a Subcategory name exists across all categories in the database
     */
    @Override
    public boolean doesSubcategoryExist(String subcategoryName) {
        Optional<Subcategory> existingSubcategory = subcategoryRepository.findByName(subcategoryName);
        return existingSubcategory.isPresent();
    }



}

