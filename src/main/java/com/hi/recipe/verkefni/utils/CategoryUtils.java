package com.hi.recipe.verkefni.utils;

import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.repository.CategoryRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CategoryUtils {
    // Extract category names from a Set of Category objects
    public static Set<String> extractCategoryNames(Set<Category> categories) {
        Set<String> categoryNames = new HashSet<>();
        for (Category category : categories) {
            categoryNames.add(category.getName());  // Extract the name of each category
        }
        return categoryNames;
    }

    // Create a category with decoding and normalization
    public static Category findOrCreateCategories(String categoryName, CategoryRepository categoryRepository) {
        // Step 1: URL decode the category name
        String decodedCategoryName = URLDecoder.decode(categoryName, StandardCharsets.UTF_8);

        // Step 2: Normalize the decoded category name (capitalize and clean up)
        String normalizedCategoryName = normalizeCategoryName(decodedCategoryName);

        // Step 3: Check if the category already exists
        Optional<Category> existingCategoryOptional = categoryRepository.findByName(normalizedCategoryName);
        if (existingCategoryOptional.isPresent()) {
            return existingCategoryOptional.get();  // Return existing category if it exists
        } else {
            // Step 4: Create and save the new category
            Category newCategory = new Category();
            newCategory.setName(normalizedCategoryName);
            return categoryRepository.save(newCategory);  // Save the new category
        }
    }

    // Normalize category name by trimming and capitalizing the first letter of each word
    public static String normalizeCategoryName(String categoryName) {
        categoryName = categoryName.trim().replaceAll("%25", "%");  // Replacing %25 with % if needed

        // Capitalize first letter of each word
        String[] words = categoryName.split(" ");
        StringBuilder normalized = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                normalized.append(Character.toUpperCase(word.charAt(0)))  // Capitalize the first letter
                        .append(word.substring(1).toLowerCase())  // Ensure the rest of the word is lowercase
                        .append(" ");  // Append the word with a space
            }
        }
        return normalized.toString().trim();  // Remove the trailing space
    }

}
