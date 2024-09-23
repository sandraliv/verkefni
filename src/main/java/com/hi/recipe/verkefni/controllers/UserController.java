package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.repository.UserRepository;
import com.hi.recipe.verkefni.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hi.recipe.verkefni.klasar.Users;

import java.util.*;

//Ef við erum að fara nota ThymeLeaf þá verður þetta @Controller en ekki @RestController og skilum ekki ResponseEntity
@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    //localhost:8000 sækir user með id 1, ef hann finnst ekki þá fæst 404 villa
    @GetMapping("/")
    public ResponseEntity<Users> getUserById() {
        Optional<Users> user = userService.findById(1);
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
