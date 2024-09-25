package com.hi.recipe.verkefni.klasar;

import jakarta.persistence.*;


@Entity
@Table(name="users")
public class Users {

    private String name;
    private String email;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    public Users(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Users(){

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

}
