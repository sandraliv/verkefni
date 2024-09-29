package com.hi.recipe.verkefni.klasar;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User {
    private String role;
    private String name;
    private String email;
    private String password;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String username;

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
