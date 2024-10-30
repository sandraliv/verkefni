package com.hi.recipe.verkefni.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Sturla er framendaforritari.");
        return "FrontPage";
    }
}