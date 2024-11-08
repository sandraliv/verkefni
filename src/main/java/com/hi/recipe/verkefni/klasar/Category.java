package com.hi.recipe.verkefni.klasar;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;  // e.g. BAKING, DESSERT

    @ManyToMany(mappedBy = "categories")
    private Set<Recipe> recipes = new HashSet<>();  // Many recipes can belong to a category

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonManagedReference  // This allows serialization of the 'subcategories' in the Category
    private Set<Subcategory> subcategories = new HashSet<>();

    // Constructors, getters, setters
    public Category() {}

    public Category(String name) {
        this.name = name;
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

    // Getter for recipes
    public Set<Recipe> getRecipes() {
        return recipes;
    }

    // Setter for recipes
    public void setRecipes(Set<Recipe> recipes) {
        this.recipes = recipes;
    }


    public Set<Subcategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(Set<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }
}
