package com.hi.recipe.verkefni.utils;


import com.hi.recipe.verkefni.klasar.Category;
import com.hi.recipe.verkefni.klasar.Subcategory;
import com.hi.recipe.verkefni.repository.SubcategoryRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;

public class SubcategoryUtils {



    public static Optional<Subcategory> getOrCreateSubcategory(String subcategoryName, Set<Category> categories, SubcategoryRepository subcategoryRepository) {
        // Step 1: URL decode the subcategory name
        String decodedSubcategoryName = URLDecoder.decode(subcategoryName, StandardCharsets.UTF_8);

        // Step 2: Normalize the decoded subcategory name
        String normalizedSubcategoryName = normalizeSubcategoryName(decodedSubcategoryName);

        // Step 3: Check for the subcategory in each category
        for (Category category : categories) {
            // Check if the subcategory exists within the category
            Optional<Subcategory> existingSubcategory = subcategoryRepository.findByNameAndCategory(normalizedSubcategoryName, category);
            if (existingSubcategory.isPresent()) {
                return existingSubcategory;  // Return existing subcategory if it exists
            }
        }

        // Step 4: If subcategory doesn't exist, create and associate it with the category
        Subcategory newSubcategory = new Subcategory();
        newSubcategory.setName(normalizedSubcategoryName);
        newSubcategory.setCategory(categories.iterator().next());  // Assuming a single category here
        subcategoryRepository.save(newSubcategory);  // Save the new subcategory
        return Optional.of(newSubcategory);  // Return newly created subcategory wrapped in Optional
    }

    // Normalize subcategory name by trimming and capitalizing first letter of each word
    public static String normalizeSubcategoryName(String subcategoryName) {
        subcategoryName = subcategoryName.trim().replaceAll("%25", "%");  // Replacing %25 with % if needed

        // Capitalize first letter of each word
        String[] words = subcategoryName.split(" ");
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
