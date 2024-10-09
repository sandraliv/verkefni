package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getUserById() {
        List<User> users = userService.findAll();

        if (users.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/addUser")
    public ResponseEntity<String> addUser(){
        User user = new User("admin", "Ásdís Stefáns", "disa@skvisa.is", "kisi111", "disaskvisa");
        userService.save(user);
        return ResponseEntity.ok("User added successfully");
    }

}
