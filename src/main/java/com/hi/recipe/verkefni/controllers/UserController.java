package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userrepo;

    public UserController(UserRepository userrepo) {
        this.userrepo = userrepo;
    }

    @GetMapping
    public ResponseEntity getAllProducts(){
        return ResponseEntity.ok(this.userrepo.findById(1));
    }
}
