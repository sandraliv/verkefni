package com.hi.recipe.verkefni.klasar;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name="recipes")
public class Recipe {
    private String title;

    private String description;

    @ElementCollection
    @MapKeyColumn(name = "ingredient_name")
    @Column(name = "ingredient_quantity")
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    private Map<String, String> ingredients = new HashMap<>();


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    public Recipe(String title, String description, Map<String, String> ingredients) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
    }

    public Recipe(){}

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getId(){
        return id;
    }

    public Map<String, String> getIngredients() {
        return ingredients;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIngredients(Map<String, String> ingredients) {
        this.ingredients = ingredients;
    }
}


