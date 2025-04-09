package com.hi.recipe.verkefni.klasar;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    private String role;
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private String username;
    private String profilePictureUrl;

    @ManyToMany(fetch = FetchType.LAZY)  // Change to EAGER loading
    @JsonIgnore
    @JoinTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private Set<Recipe> favourites = new HashSet<>();

    // The map stores the ratings where the key is the Recipe and the value is the rating score
    @ElementCollection
    @JsonIgnore
    @CollectionTable(name = "user_ratings", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyJoinColumn(name = "recipe_id")
    @Column(name = "score")
    private Map<Recipe, Integer> userRatings = new HashMap<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserRecipe> userRecipes = new HashSet<>();




    public User() {
    }

    public User(String role, String name, String email, String password, String username) {
        this.role = role;
        this.name = name;
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
    public Set<UserRecipe> getUserRecipes() {
        return userRecipes;
    }
    public void setUserRecipes(Set<UserRecipe> userRecipes) {
        this.userRecipes = userRecipes;
    }

    public Set<Recipe> getFavourites() {
        if (favourites == null) {
            favourites = new HashSet<>();
        }
        return favourites;
    }

    public void setFavourites(Recipe recipe) {
        this.favourites.add(recipe);
    }

    public void removeFavourite(Recipe recipe) {
        if (favourites != null) {
            favourites.remove(recipe);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Map<Recipe, Integer> getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(Map<Recipe, Integer> userRatings) {
        this.userRatings = userRatings;
    }

    public boolean hasRated(Recipe recipe) {
        return this.userRatings.containsKey(recipe);
    }

    public void addUserRating(Recipe recipe, int score) {
        this.userRatings.put(recipe, score);
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }


}
