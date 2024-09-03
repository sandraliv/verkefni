package com.hi.recipe.verkefni;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetController {
    @RequestMapping("/")
    public User getGreeting(){
        return new User("Sandra Liv", "Rice Pudding");
    }

    @RequestMapping("/Get")
    public Recipe getBody(){
       return new Recipe("Eggjahræra", "2 egg");
    }
}
