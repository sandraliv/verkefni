package com.hi.recipe.verkefni.klasar;

import jakarta.persistence.*;

@Entity
@Table(name="recipes")
public class Recipe {
    //Þessi klasi er ekki rétt uppsettur, tímabundin uppsetning
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String description;

    public Recipe(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Recipe(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}


