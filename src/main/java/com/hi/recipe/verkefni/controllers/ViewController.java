package com.hi.recipe.verkefni.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import com.hi.recipe.verkefni.services.RecipeService;


@Controller
public class ViewController {
    private final RecipeService recipeService;

    public ViewController(RecipeService recipeService){
        this.recipeService = recipeService;
    }
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Sturla er framendaforritari.");
        model.addAttribute("recipes", recipeService.findAllPaginated());
        return "FrontPage";
    }
}