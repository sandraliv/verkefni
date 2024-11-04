package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.services.RecipeService;
import com.hi.recipe.verkefni.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Map;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    //================================================================================
    // GET Methods
    //================================================================================

    /**
     * Retrieves all users in the system
     * @return List of all users or empty list if none exist
     */
    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a specific user's profile by their ID
     * @param id The unique identifier of the user
     * @return The requested user profile if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserProfileById(@PathVariable int id) {
        Optional<User> userProfile = userService.findById(id);
        return userProfile.isPresent() 
            ? ResponseEntity.ok(userProfile)
            : ResponseEntity.notFound().build();
    }

    /**
     * Retrieves favorite recipes for the currently logged-in user
     * @param session The current HTTP session containing user information
     * @return List of favorite recipes if user is logged in, or 401 if not authenticated
     */
    @GetMapping("/getUserFav")
    public ResponseEntity<List<Recipe>> getUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<Recipe> favs = user.getFavourites();
            return ResponseEntity.ok().body(favs);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    //================================================================================
    // POST Methods
    //================================================================================

    /**
     * Registers a new user in the system
     * @param user The user object containing registration details
     * @return Success message if user created, error if user already exists
     */
    @PostMapping("/Register")
    public ResponseEntity<String> newUser(@RequestBody User user) {
        // Check if user already exists by email or username
        Optional<User> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already registered with this email.");
        }

        // Save the new user if not already registered
        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("User added successfully!");
    }


    /**
     * Authenticates a user and creates a session
     * @param session The HTTP session to store user information
     * @param user The user credentials for authentication
     * @return Success message if login successful, error if credentials invalid or user not found
     */
    @PostMapping("Login")
    public ResponseEntity<String> login(HttpSession session, @RequestBody User user){
        Optional<User> ou = userService.findByUsername(user.getUsername());
        if(ou.isPresent()){
            User u = ou.get();
            if(u.getPassword().equals(user.getPassword())) {
                session.setAttribute("user", u);
                return ResponseEntity.status(HttpStatus.FOUND).body("Login successful");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad credentials");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    //================================================================================
    // DELETE Methods
    //================================================================================

    /**
     * Removes a user from the system
     * @param id The ID of the user to delete
     * @return Success message if deleted
     */
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id){
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }

    /**
     * Removes a recipe from the current user's favorites list.
     * @param recipeId The ID of the recipe to remove from favorites
     * @param session The HTTP session to identify the logged-in user
     * @return Success message if removed, 404 if recipe or user not found, or 401 if not logged in
     */
    @DeleteMapping("/removeFavorite/{recipeId}")
    public ResponseEntity<String> removeFavoriteRecipe(@PathVariable int recipeId, HttpSession session) {
        // Get the current logged-in user from the session
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
        }
    
        // Find the recipe to remove
        Optional<Recipe> recipeToRemove = user.getFavourites().stream()
                .filter(recipe -> recipe.getId() == recipeId)
                .findFirst();
        
        // Check if recipe exists in user's favorites
        if (recipeToRemove.isPresent()) {
            user.getFavourites().remove(recipeToRemove.get());
            userService.save(user);  // Persist changes to the database
            return ResponseEntity.ok("Recipe removed from favorites.");
        }
    
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Recipe not found in favorites.");
    }
  /**
 * Updates certain details in a user's profile  
 * @param id The ID of the user to update
 * @param updates A Map containing the fields to update and their new values
 * @return Success message if updated, 404 if user not found
 */
@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
public ResponseEntity<String> updateUserProfile(@PathVariable int id, @RequestBody Map<String, Object> updates) {
    Optional<User> userOptional = userService.findById(id);
    if (userOptional.isPresent()) {
        User user = userOptional.get();

        // updates neme
        if (updates.containsKey("name")) {
            user.setName((String) updates.get("name"));
        }

        // updates email
        if (updates.containsKey("email")) {
            user.setEmail((String) updates.get("email"));
        }

        //updated name and email saved
        userService.save(user);
        return ResponseEntity.ok("Nafn og email uppfært");
    } else {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notandi fannst ekki");
    }
}

}