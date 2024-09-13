package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hi.recipe.verkefni.klasar.Users;

import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    //UserRepository connects to the recipe table because it is tied to the User entity, which is mapped to that table.
    //UserRepository in your controller interacts with the User table through the repository methods provided by JpaRepository.
    private final UserRepository userrepo;

    //Here we are injecting the UserRepo dependency into the controller, which allows the controller to interact with the database through the repository.
    public UserController(UserRepository userrepo) {
        this.userrepo = userrepo;
    }

    //Get method for
    @GetMapping
    public ResponseEntity<Users> getUserById() {
        Optional<Users> user = userrepo.findById(1);  // findById returns Optional<User>

        // Check if the user is present
        // Return 200 OK with the user object
        // Return 404 if the user is not found
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Slóðin localhost:8000/users/addUSer addaði mér inní gagnagrunninn með þessari aðferð hér
    /*
    @GetMapping("/addUSer")
    public ResponseEntity<String> addUser(){
        Users user = new Users("Sandra Liv Sigurðardóttir", "sandralivsig@gmail.com");
        userrepo.save(user);
        return ResponseEntity.ok("User added successfully");
    }
     */

}
