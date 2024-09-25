package com.hi.recipe.verkefni.klasar;

import jakarta.persistence.*;

@Entity
@Table(name="recipes")
public class Recipe {
    //Þessi klasi er ekki rétt uppsettur, tímabundin uppsetning
    @Id
    private String title;

    private String description;

    public Recipe(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Recipe(){}

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}


