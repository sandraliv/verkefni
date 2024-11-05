package com.hi.recipe.verkefni.uiControllers;

import com.hi.recipe.verkefni.services.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIrecipeController {
    private final RecipeService recipeService;

    public UIrecipeController(RecipeService recipeService){
        this.recipeService = recipeService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Sturla er framendaforritari.");
        model.addAttribute("recipes", recipeService.findAllPaginated());
        return "FrontPage";
    }
}
