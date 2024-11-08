package com.hi.recipe.verkefni.services;

import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.Subcategory;
import com.hi.recipe.verkefni.repository.CategoryRepository;
import com.hi.recipe.verkefni.repository.RecipeRepository;
import com.hi.recipe.verkefni.repository.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final RecipeRepository recipeRepository;
    private final RecipeService recipeService;

    // Constructor injection for CategoryRepository
    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, RecipeRepository recipeRepository, SubcategoryRepository subcategoryRepository, RecipeService recipeService) {
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.recipeRepository = recipeRepository;
        this.recipeService = recipeService;
    }

    // Find category by its name
    @Override
    public Optional<Category> getCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName);
    }
    @Override
    public Category createCategoryIfNotExists(String categoryName) {
        Optional<Category> existingCategory = getCategoryByName(categoryName);
        if (existingCategory.isEmpty()) {
            Category newCategory = new Category();
            newCategory.setName(categoryName);
            return categoryRepository.save(newCategory);
        }
        return existingCategory.get();  // Return the existing category if found
    }

    // Create a new category
    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);  // Save the new category and return it
    }

    // Get Category associated with a Recipe by Recipe ID
    @Override
    public List<Category> getCategoriesByRecipeId(Long recipeId) {
        return categoryRepository.findCategoriesByRecipeId(recipeId);
    }

    // Find all Subcategories by Category ID
    @Override
    public List<Subcategory> getSubcategoriesByCategoryId(Long categoryId) {
        return categoryRepository.findSubcategoriesByCategoryId(categoryId);
    }
    @Override
    public Set<String> getSubcategoryNamesForRecipe(Recipe recipe) {
        // Assuming the recipe has a valid ID and is persisted in the database
        return recipeRepository.findSubcategoryNamesByRecipe(recipe);
    }

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
    // Update an existing category (name)
    @Override
    public Category updateCategory(Long id, Category category) {
        // Fetch the existing category from the database
        Optional<Category> existingCategoryOptional = categoryRepository.findById(id);

        if (existingCategoryOptional.isPresent()) {
            Category existingCategory = existingCategoryOptional.get();

            // Update the category properties (you can update any other properties here)
            existingCategory.setName(category.getName());  // Assuming you're updating the name

            // Save the updated category and return it
            return categoryRepository.save(existingCategory);
        } else {
            return null;  // Return null if the category doesn't exist
        }
    }
    @Override
    public boolean deleteCategory(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    // Search Category by Name Pattern
    @Override
    public List<Category> searchCategoriesByName(String namePattern) {
        return categoryRepository.findByNameContainingIgnoreCase(namePattern);
    }


    // Helper method to get the first subcategory name associated with the recipe
    @Override
    public String getSubcategoryName(Recipe recipe) {
        if (!recipe.getSubcategories().isEmpty()) {
            return recipe.getSubcategories().iterator().next().getName();
        }
        return "No subcategory";  // Default value if no subcategory exists
    }
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    // Method to find categories by their names (expects Set<String>)
    public Set<Category> findCategoriesByNames(Set<String> categoryNames) {
        // Make sure to pass category names (Set<String>) to the repository
        List<Category> categories = categoryRepository.findByNameIn(categoryNames);
        return new HashSet<>(categories);  // Convert to Set if needed
    }

    @Override
    public Set<String> extractCategoryNames(Set<Category> categories) {
        return categories.stream()
                .map(Category::getName)  // Assuming Category has a getName() method
                .collect(Collectors.toSet());
    }


}
