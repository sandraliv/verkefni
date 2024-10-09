package com.hi.recipe.verkefni.klasar;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="users")
public class User {
    private String role;
    private String name;
    private String email;
    private String password;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true)
    private String username;

   @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
   @JoinTable(name = "user_recipes",
   joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {@JoinColumn(name = "recipe_id", referencedColumnName = "id")})
   private List<Recipe> favourites;

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

    public List<Recipe> getFavourites(){
        return favourites;
    }

    public void setFavourites(Recipe recipe ){
        favourites.add(recipe);
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
