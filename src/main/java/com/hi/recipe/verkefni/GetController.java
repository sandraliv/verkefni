package com.hi.recipe.verkefni;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetController {
    @RequestMapping("/")
    public String getGreeting(){
        return "Hi, its me Sandra";
    }
}
