package com.hi.recipe.verkefni.klasar;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name="recipes")
public class Recipe {
    private String title;

    private String description;

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
    @CollectionTable(name = "recipe_tags", joinColumns = @JoinColumn(name = "recipe_id"), uniqueConstraints =  @UniqueConstraint(columnNames = {"recipe_id", "tags"}))
    @Column(name = "tag")  // Define the column name for tags
    private Set<RecipeTag> tags = new HashSet<>();  // Multiple tags

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private LocalDateTime dateAdded;

    @ManyToMany(mappedBy = "favourites")
    private List<User> userList;

    public Recipe(String title, String description, Map<String, String> ingredients, Set<RecipeTag> tags) {
        this.title = title;
        this.description = description;
        this.ingredients = ingredients;
        this.tags = tags;
    }

    public Recipe(){}

    protected void onCreate() {
        this.dateAdded = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
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

    public int getId(){
        return id;
    }

    public void setDescription(String description) {
        this.description = this.description;
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
}


