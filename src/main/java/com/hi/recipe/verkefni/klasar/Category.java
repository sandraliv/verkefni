package com.hi.recipe.verkefni.klasar;

public enum Category {
    BREAKFAST,
    APPETIZER,
    MAIN_COURSE,
    SNACK,
    DESSERT,
    BAKING;


    public String getDisplayName() {
        return name().charAt(0) + name().substring(1).toLowerCase().replace('_', ' ');
    }
}
