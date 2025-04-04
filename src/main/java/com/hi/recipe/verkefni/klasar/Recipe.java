package com.hi.recipe.verkefni.klasar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Entity
@Table(name = "recipes",
        indexes = {
                @Index(name = "idx_avg_rating", columnList = "averageRating DESC"),
                @Index(name = "idx_date_added", columnList = "dateAdded DESC")
        })
public class Recipe {
    @Column(name = "title")
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_images", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "saved_to_calendar_count", nullable = false)
    private int savedToCalendarCount = 0; // Default to 0


    @Column(name = "description", length = 500)
    private String description;
    @Column(name = "instructions", length = 500)
    private String instructions;


    private String formattedDate;

    @ElementCollection(fetch = FetchType.LAZY
    )
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @MapKeyColumn(name = "ingredient_name")
    @Column(name = "ingredient_quantity")
    @NotEmpty
    private Map<String, String> ingredients = new HashMap<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "recipe_tags",
            joinColumns = @JoinColumn(name = "recipe_id")
    )
    @BatchSize(size = 30)
    @Column(name = "tag")
    @Enumerated(EnumType.STRING)  // Make sure this is STRING, not ORDINAL
    private Set<RecipeTag> tags = new HashSet<>();


    @ElementCollection(targetClass = Category.class, fetch = FetchType.LAZY)  // To store multiple categories
    @Enumerated(EnumType.STRING)  // Store enums as strings in the database
    @CollectionTable(name = "recipe_categories", joinColumns = @JoinColumn(name = "recipe_id"), uniqueConstraints = @UniqueConstraint(columnNames = {"recipe_id", "category"}))
    @Column(name = "category")
    @BatchSize(size = 30)
    private Set<Category> categories = new HashSet<>();  // Multiple categories

    @PreRemove
    public void preRemove() {
        // Remove the recipe from all users' ratings (i.e., userRatings)
        for (User user : recipeRatings.keySet()) {
            // Remove the recipe from the user's ratings map (i.e., userRatings)
            user.getUserRatings().remove(this);  // Remove this recipe from the user's ratings
        }

        recipeRatings.clear();
        categories.clear();
    }

    @ManyToMany(mappedBy = "favourites")
    @JsonIgnore
    private Set<User> userFavorites = new HashSet<>();

    @Transient
    private boolean isFavoritedByUser;


    @ElementCollection
    @JsonIgnore
    @CollectionTable(name = "recipe_ratings", joinColumns = @JoinColumn(name = "recipe_id"))
    @MapKeyJoinColumn(name = "user_id")
    @Column(name = "score")
    private Map<User, Integer> recipeRatings = new HashMap<>();


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(precision = 3, scale = 2, nullable = false) // Now using BigDecimal
    private BigDecimal averageRating = BigDecimal.ZERO; // Default to 0.0// Use BigDecimal for average rating

    @Column(name = "rating_count")
    private Integer ratingCount = 0;  // The count of ratings

    @CreationTimestamp
    private LocalDateTime dateAdded;

    public Recipe(String title, String description, Map<String, String> ingredients, Set<RecipeTag> tags, Set<Category> categories, String instructions) {
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

    // Getter and setter for imageUrls
    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }


    // Method to clear all ratings from the recipe
    public void clearRatings() {
        this.recipeRatings.clear();
    }

    public Integer getRatingCount() {
        if (recipeRatings != null) {
            return recipeRatings.size();
        }
        return 0;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Map<User, Integer> getRecipeRatings() {
        return recipeRatings;
    }

    public void setRecipeRatings(Map<User, Integer> recipeRatings) {
        this.recipeRatings = recipeRatings;
    }

    public void addRating(User user, int score) {
        this.recipeRatings.put(user, score);
        this.ratingCount = this.recipeRatings.size();
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
        if (!recipeRatings.isEmpty()) {
            // Filter out null values and calculate the sum of the ratings
            BigDecimal sum = recipeRatings.values().stream()
                    .filter(Objects::nonNull)  // Filter out any null ratings
                    .map(BigDecimal::valueOf)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            this.averageRating = sum.divide(BigDecimal.valueOf(recipeRatings.size()), 2, RoundingMode.HALF_UP);
        } else {
            this.averageRating = BigDecimal.ZERO;
        }
    }

    public Set<User> getUserFavorites() {
        if (userFavorites == null) {
            userFavorites = new HashSet<>();
        }
        return userFavorites;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public void setUserFavorites(Set<User> userFavorites) {
        this.userFavorites = userFavorites;
    }

    public boolean isFavoritedByUser() {
        return isFavoritedByUser;
    }

    public void setIsFavoritedByUser(boolean isFavoritedByUser) {
        this.isFavoritedByUser = isFavoritedByUser;
    }

    public int getSavedToCalendarCount() {
        return savedToCalendarCount;
    }

    public void setSavedToCalendarCount(int savedToCalendarCount) {
        this.savedToCalendarCount = savedToCalendarCount;
    }

    public String getFormattedDate() {
        if (dateAdded != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd,MMMM,yyyy");
            return dateAdded.format(formatter);
        }
        return null;
    }
}

