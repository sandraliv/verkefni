package com.hi.recipe.verkefni.klasar;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name="recipes")
public class Recipe {
    private String title;
    private String image_url;
    private String description;
    private String instructions;


    @ElementCollection(fetch = FetchType.EAGER)
    @MapKeyColumn(name = "ingredient_name")
    @Column(name = "ingredient_quantity")
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    private Map<String, String> ingredients = new HashMap<>();


    /*Tells hibernate that the tags field is a collection of values that will be stored in a separate table and we are using the RecipeTag enum*/
    //The CollectionTable defines the table(recipe_tags) and column(recipe_id) that will store the relationship between recipes and their tags.
    //The set ensures that there are no duplicates for a recipe
    @ElementCollection(targetClass = RecipeTag.class, fetch = FetchType.EAGER)  // To store multiple tags
    @Enumerated(EnumType.STRING)  // Store enums as strings in the database
    @CollectionTable(name = "recipe_tags", joinColumns = @JoinColumn(name = "recipe_id"), uniqueConstraints = @UniqueConstraint(columnNames = {"recipe_id", "tags"}))
    @Column(name = "tag")  // Define the column name for tags
    private Set<RecipeTag> tags = new HashSet<>();  // Multiple tags


    @ElementCollection(targetClass = Category.class, fetch = FetchType.EAGER)  // To store multiple categories
    @Enumerated(EnumType.STRING)  // Store enums as strings in the database
    @CollectionTable(name = "recipe_newCategories", joinColumns = @JoinColumn(name = "recipe_id"), uniqueConstraints = @UniqueConstraint(columnNames = {"recipe_id", "newCategory"}))
    @Column(name = "newCategory")  // Define the column name for categories
    private Set<Category> categories = new HashSet<>();  // Multiple categories


    // Use ElementCollection to store ratings temporarily
    @ElementCollection
    @CollectionTable(
            name = "temp_recipe_ratings", // Temporary table for ratings
            joinColumns = @JoinColumn(name = "temp_recipe_id") // Temporary foreign key to Recipe
    )
    @Column(name = "temp_score") // Temporary column for storing rating scores
    private Set<Integer> tempRatings = new HashSet<>(); // Temporary set of ratings

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(precision = 3, scale = 2,nullable = false) // Now using BigDecimal
    private BigDecimal averageRating = BigDecimal.ZERO; // Default to 0.0// Use BigDecimal for average rating

    @Column(name = "rating_count")
    private Integer ratingCount = 0;  // The count of ratings

    @ManyToMany(mappedBy = "favourites")
    private List<User> userList;

    @CreationTimestamp
    private LocalDateTime dateAdded;



    public Recipe(String title, String description, Map<String, String> ingredients, Set<RecipeTag> tags,Set<Category> categories ,String instructions) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.tags = tags;
        this.instructions = instructions;
        this.categories = categories;

    }

    public Recipe() {
    }

    public String getTitle() {
        return title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }


    // Getter and Setter for ratingCount
    public Integer getRatingCount() {
        if (tempRatings != null) {
            return tempRatings.size();  // Dynamically calculate count from tempRatings set
        }
        return 0;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Set<Integer> getTempRatings() {
        return tempRatings;
    }

    public void setTempRatings(Set<Integer> tempRatings) {
        this.tempRatings = tempRatings;
    }

    // Method to add a rating and recalculate the average
    public void addTempRating(int score) {
        this.tempRatings.add(score);
        this.ratingCount = this.tempRatings.size();
        recalculateAverageRating();
    }

    public Collection<RecipeTag> getTags() {
        return tags;
    }

    public void setTags(Set<RecipeTag> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
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

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

   // Recalculate the average rating from the temporary ratings
   public void recalculateAverageRating() {
       if (!tempRatings.isEmpty()) {
           BigDecimal sum = tempRatings.stream()
                   .map(BigDecimal::valueOf)
                   .reduce(BigDecimal.ZERO, BigDecimal::add);
           this.averageRating = sum.divide(BigDecimal.valueOf(tempRatings.size()), 2, RoundingMode.HALF_UP);
       } else {
           this.averageRating = BigDecimal.ZERO;
       }
   }


}

