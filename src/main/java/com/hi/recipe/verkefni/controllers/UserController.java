package com.hi.recipe.verkefni.controllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.services.ImageService;
import com.hi.recipe.verkefni.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public UserController(UserService userService, ImageService imageService){
        this.userService = userService;
        this.imageService = imageService;
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
    @Transactional(readOnly = true)  // Add this
    public ResponseEntity<User> getUser(HttpSession session) {
        User user = (User) session.getAttribute("LoggedInUser");
        if (user != null) {
            // Get a fresh copy from the database
            User freshUser = userService.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(freshUser);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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


    @PostMapping("/{id}/add_profile_pic")
    public ResponseEntity<Map<String, String>> addPic(@PathVariable int id,
                                                      @RequestParam("file") MultipartFile file) throws IOException {
        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            // Upload image to Cloudinary and get the URL
            String imageUrl = imageService.uploadImage(file);

            // Update the existing user's profile picture URL
            existingUser.setProfilePictureUrl(imageUrl);
            userService.save(existingUser); // Save the updated user

            // Return only the updated profile picture URL as JSON
            return ResponseEntity.ok(Collections.singletonMap("profilePictureUrl", imageUrl));
        }
        // If user is not found, return a JSON error response
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "User not found"));
    }



    /**
     * Authenticates a user and creates a session
     * @param session The HTTP session to store user information
     * @param user The user credentials for authentication
     * @return Success message if login successful, error if credentials invalid or user not found
     */
    @PostMapping("Login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> optionalUser = userService.findByUsername(user.getUsername());
    
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "User not found."));
        }
    
        User foundUser = optionalUser.get();
        if (!foundUser.getPassword().equals(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Invalid credentials."));
        }
    
        return ResponseEntity.ok(optionalUser);
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
 * Updates the password of a logged in user
 * 
 * @param session Checks if the user is logged in.
 * @param updates A Map containing "currentPassword", "newPassword", and "confirmNewPassword" keys.
 * @return A success message if the password is changed, or an error messages.
 */
@PatchMapping("/updatePassword")
public ResponseEntity<String> updatePassword(HttpSession session, @RequestBody Map<String, String> updates) {
    
    User user = (User) session.getAttribute("user");
    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
    }


    String currentPassword = updates.get("currentPassword");
    if (!user.getPassword().equals(currentPassword)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect.");
    }

    String newPassword = updates.get("newPassword");
    String confirmNewPassword = updates.get("confirmNewPassword");
    if (newPassword == null || !newPassword.equals(confirmNewPassword)) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("New passwords do not match.");
    }


    user.setPassword(newPassword);
    userService.save(user);

    return ResponseEntity.ok("Password changed successfully");
}
}

