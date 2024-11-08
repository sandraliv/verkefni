package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.ContactForm;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import com.hi.recipe.verkefni.services.RecipeService;


@RestController
@RequestMapping("/api")
public class ContactController {
    @PostMapping("/contact_us")
    public ResponseEntity<String> submitContactForm(@Valid @RequestBody ContactForm contactForm) {
        // Process form data here, e.g., save to database or send an email
        // For this example, we'll just return a confirmation message

        return ResponseEntity.status(200).body("Contact form submitted successfully!");
    }
}