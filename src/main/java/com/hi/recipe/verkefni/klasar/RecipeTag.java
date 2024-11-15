package com.hi.recipe.verkefni.klasar;

public enum RecipeTag {
    VEGETARIAN,
    VEGAN,
    GLUTEN_FREE,
    KETO,
    DAIRY_FREE,
    LOW_CARB;

    // Method to return a human-readable version of the enum name
    public String getDisplayName() {
        // Capitalize the first letter and replace underscores with spaces
        return name().charAt(0) + name().substring(1).toLowerCase().replace('_', ' ');
    }
}
