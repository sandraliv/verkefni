package com.hi.recipe.verkefni.klasar;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

@Entity
@Table(name = "user_recipes")
public class UserRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Title is required")
    private String title;

    private String image_url;

    @NotBlank(message = "Description is required")
    @Column(length = 500) // This will map the description to VARCHAR(500)
    private String description;

    @NotBlank(message = "Please add instructions")
    @Column(length = 500) // This will map the instructions to VARCHAR(500)
    private String instructions;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_recipe_ingredients", joinColumns = @JoinColumn(name = "user_recipe_id"))
    @MapKeyColumn(name = "ingredient_name")
    @Column(name = "ingredient_quantity")
    private Map<String, String> ingredients;

    // Many-to-One relation with the User (user who uploaded the recipe)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructor
    public UserRecipe() {
    }

    public UserRecipe(String title, String description, Map<String, String> ingredients, String instructions) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Map<String, String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Map<String, String> ingredients) {
        this.ingredients = ingredients;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
