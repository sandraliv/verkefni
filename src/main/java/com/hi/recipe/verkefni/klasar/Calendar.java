package com.hi.recipe.verkefni.klasar;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;  // This is for public recipes

    @ManyToOne
    @JoinColumn(name = "user_recipe_id")
    private UserRecipe userRecipe;  // This is for user-created recipes (UserRecipe)

    @Column(name = "saved_calendar_date", nullable = false)
    private LocalDate savedCalendarDate;  // Store only the date

    // Constructor, Getters, and Setters
    public Calendar() {}

    public Calendar(User user, Recipe recipe, UserRecipe userRecipe, LocalDate savedCalendarDate) {
        this.user = user;
        this.recipe = recipe;
        this.userRecipe = userRecipe;
        this.savedCalendarDate = savedCalendarDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public UserRecipe getUserRecipe() {
        return userRecipe;
    }

    public void setUserRecipe(UserRecipe userRecipe) {
        this.userRecipe = userRecipe;
    }

    public LocalDate getSavedCalendarDate() {
        return savedCalendarDate;
    }

    public void setSavedCalendarDate(LocalDate savedCalendarDate) {
        this.savedCalendarDate = savedCalendarDate;
    }
}
