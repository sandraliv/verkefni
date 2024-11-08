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


    @ManyToMany
    @JoinTable(
            name = "recipe_categories",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> categories = new HashSet<>();  // A recipe can belong to multiple categories

    @ManyToMany
    @JoinTable(
            name = "recipe_subcategories",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "subcategory_id")
    )
    private Set<Subcategory> subcategories = new HashSet<>(); // Subcategories that this recipe belongs to

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

    /*@OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<Rating> ratings;*/

    @ManyToMany(mappedBy = "favourites")
    private List<User> userList;

    @CreationTimestamp
    private LocalDateTime dateAdded;



    public Recipe(String title, String description, Map<String, String> ingredients, Set<RecipeTag> tags, Set<Subcategory> subcategories,Set<Category> categories, String instructions) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.tags = tags;
        this.subcategories = subcategories;
        this.categories = categories;
        this.instructions = instructions;

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

    public Set<Subcategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(Set<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
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

    /* public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public void addRating(Rating rating) {
        this.ratings.add(rating);
        recalculateAverageRating();
    }

   /* public double getAverageRating() {
        return ratings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);
    }*/

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public Set<String> getCategoryNames() {
        Set<String> names = new HashSet<>();
        for (Category category : categories) {
            names.add(category.getName());
        }
        return names;
    }

   /* public void recalculateAverageRating() {
        if (ratings.isEmpty()) {
            this.averageRating = BigDecimal.ZERO; // Set to zero if there are no ratings
        } else {
            BigDecimal sum = BigDecimal.ZERO;

            // Sum all the ratings
            for (Rating rating : ratings) {
                sum = sum.add(BigDecimal.valueOf(rating.getScore())); // Assuming getRating returns an int
            }

            // Calculate the average
            this.averageRating = sum.divide(BigDecimal.valueOf(ratings.size()), 2, RoundingMode.HALF_UP);
        }
    }*/
  /* public void recalculateAverageRating() {
       if (ratings.isEmpty()) {
           this.averageRating = BigDecimal.ZERO;
       } else {
           double avg = ratings.stream()
                   .mapToInt(Rating::getScore)
                   .average()
                   .orElse(0.0);

           // Convert the double result to BigDecimal and set the scale
           this.averageRating = BigDecimal.valueOf(avg).setScale(2, BigDecimal.ROUND_HALF_UP);
       }
   }*/
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

