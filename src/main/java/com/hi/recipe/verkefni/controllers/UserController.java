package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

//Ef við erum að fara nota ThymeLeaf þá verður þetta @Controller en ekki @RestController og skilum ekki ResponseEntity
@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    /* Notum þetta og breytum úr RestController í Controller þegar við viljum byrja nota Thymeleaf
    @GetMapping("/")
    public String getUsers(Model model){
        model.addAttribute("name", "Jóhanna");
        return "User";
    }

     */

    @GetMapping("/")
    public ResponseEntity<List<User>> getUserById() {
        List<User> users = userService.findAll();

        if (users.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(users);
    }



    //Slóðin localhost:8000/users/addUSer addaði mér inní gagnagrunninn með þessari aðferð hér

    @GetMapping("/addUser")
    public ResponseEntity<String> addUser(){
        User user = new User("admin", "Ásdís Stefáns", "disa@skvisa.is", "kisi111", "disaskvisa");
        userService.save(user);
        return ResponseEntity.ok("User added successfully");
    }

    @PostMapping("/Register")
    public ResponseEntity<String> newUser(@RequestBody User user) {
        // Assuming that the recipe entity has appropriate constructors or setters.
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User added successfully!");
    }

}
