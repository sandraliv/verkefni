package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.services.RecipeService;
import com.hi.recipe.verkefni.services.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


//Ef við erum að fara nota ThymeLeaf þá verður þetta @Controller en ekki @RestController og skilum ekki ResponseEntity
@RestController
@RequestMapping("/users")
public class UserController {
    private final RecipeService recipeService;
    private final UserService userService;

    @Autowired
    public UserController(UserService userService, RecipeService recipeService){
        this.userService = userService;
        this.recipeService = recipeService;
    }

    //Get Users - http://localhost:8000/
    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(users);
    }

    /*This needs fixing - should be Post method 
    @GetMapping("/addUser")
    public ResponseEntity<String> addUser(){
        User user = new User("admin", "Ásdís Stefáns", "disa@skvisa.is", "kisi111", "disaskvisa");
        userService.save(user);
        return ResponseEntity.ok("User added successfully");
    } */

    //This needs fixing - should be Patch method
    @GetMapping("/addFavourite")
    public ResponseEntity<String> addFavourite(){
        Optional<Recipe> recipe = recipeService.findById(52);
        Optional<User> user = userService.findById(2);
        Recipe recipe2 = null;
        if (recipe.isPresent()) {
            recipe2 = recipe.get();
            System.out.println(recipe2.getTags());

        }
        if (user.isPresent()){
            User users = user.get();
            users.setFavourites(recipe2);
            userService.save(users);
            System.out.println("Hellooo");
        }
        return ResponseEntity.ok("User added successfully");
    }

    //Need handler for bad request
    @PostMapping("/Register")
    public ResponseEntity<String> newUser(@RequestBody User user) {
        // Assuming that the recipe entity has appropriate constructors or setters.
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User added successfully!");
    }

    @PostMapping("Login")
    public ResponseEntity<String> login(HttpSession session, @RequestBody User user){

        Optional<User> ou = userService.findByUsername(user.getUsername());
        if(ou.isPresent()){
            User u = ou.get();
            if(u.getPassword().equals(user.getPassword())) {
                session.setAttribute("user", u);
                return ResponseEntity.status(HttpStatus.FOUND).body("Login successful");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad credentials");
            }
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @GetMapping("/getUserFav")
    public ResponseEntity<List<Recipe>> getUser(HttpSession session) {
        User user = (User) session.getAttribute("user");  // Retrieve the user from the session
        System.out.println("check1");
        System.out.println(user.getFavourites());
        if (user != null) {
            List<Recipe> favs = user.getFavourites();
            System.out.println(favs);
            return ResponseEntity.ok().body(favs);  // Return the user object if found
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);  // If no user is found, return an error
        }
    }

    // http://localhost:8000/users/2
    // Get user profile by ID - http://localhost:8000/users/2
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserProfileById(@PathVariable int id) {
        Optional<User> userProfile = userService.findById(id);
        
        if (userProfile.isPresent()) {
            return ResponseEntity.ok(userProfile);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id){
        userService.deleteById(id);
    }

    /* Notum þetta og breytum úr RestController í Controller þegar við viljum byrja nota Thymeleaf
    @GetMapping("/")
    public String getUsers(Model model){
        model.addAttribute("name", "Jóhanna");
        return "User";
    }
     */
}
