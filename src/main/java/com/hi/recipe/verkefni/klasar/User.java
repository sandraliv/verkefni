package com.hi.recipe.verkefni.klasar;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="users")
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

    @ManyToMany(fetch = FetchType.EAGER)  // Change to EAGER loading
    @JoinTable(
        name = "user_recipes",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private List<Recipe> favourites = new ArrayList<>();


    public User(){
    }

    public User(String role, String name, String email, String password, String username){
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

    public String getRole() {return role;}

    public String getPassword(){return password;}

    public String getUsername(){return username;}

    public List<Recipe> getFavourites() {
        if (favourites == null) {
            favourites = new ArrayList<>();
        }
        return favourites;
    }

    public void setFavourites(List<Recipe> favourites) {
        this.favourites = favourites;
    }

    public void addFavourite(Recipe recipe) {
        if (favourites == null) {
            favourites = new ArrayList<>();
        }
        favourites.add(recipe);
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


}
