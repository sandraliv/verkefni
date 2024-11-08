package com.hi.recipe.verkefni.klasar;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;  // e.g. COOKIES, CAKES

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference  // This prevents infinite recursion by not serializing the 'category' in Subcategory
    private Category category;

    @ManyToMany(mappedBy = "subcategories")
    private Set<Recipe> recipes = new HashSet<>(); // Recipes that belong to this subcategory


    // Default constructor (necessary for JPA)
    public Subcategory() {}

    public Subcategory(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(Set<Recipe> recipes) {
        this.recipes = recipes;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}


