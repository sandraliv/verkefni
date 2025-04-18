package com.hi.recipe.verkefni.klasar;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "user_recipes")
public class UserRecipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Title is required")
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_recipe_images", joinColumns = @JoinColumn(name = "user_recipe_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();


    @NotBlank(message = "Description is required")
    @Column(length = 500)
    private String description;

    @NotBlank(message = "Please add instructions")
    @Column(length = 500)
    private String instructions;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_recipe_ingredients", joinColumns = @JoinColumn(name = "user_recipe_id"))
    @MapKeyColumn(name = "ingredient_name")
    @Column(name = "ingredient_quantity")
    private Map<String, String> ingredients;

    @CreationTimestamp
    private LocalDateTime dateAdded;
    @Transient
    private String formattedDate;

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

    // Getter and setter for imageUrls
    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void addImageUrl(String imageUrl) {
        if (this.imageUrls == null) {
            this.imageUrls = new ArrayList<>();
        }
        this.imageUrls.add(imageUrl);
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
    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
        // Format the date whenever it's set
        this.formattedDate = formatDate(dateAdded);
    }

    // Format LocalDateTime to a readable string
    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss");
        return date.format(formatter);
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    // This ensures that if we want to override the formattedDate field, we can do so here
    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}
