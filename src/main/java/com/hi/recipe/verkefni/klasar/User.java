package com.hi.recipe.verkefni.klasar;

public class User {
    private static int idCounter = 0; // Static counter to track IDs

    private String userName;
    private String favouriteFood;
    private int id;

    public User(String name, String food) {
        this.userName = name;
        this.favouriteFood = food;
        this.id = idCounter++; // Assign the current counter value and increment it
    }

    public String getUserName() {
        return userName;
    }

    public String getFavouriteFood() {
        return favouriteFood;
    }

    public int getId() {
        return id;
    }

}
