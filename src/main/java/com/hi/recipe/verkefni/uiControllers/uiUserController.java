package com.hi.recipe.verkefni.uiControllers;

import com.hi.recipe.verkefni.klasar.Recipe;
import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.services.ImageService;
import com.hi.recipe.verkefni.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("usersui")
public class uiUserController {

    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public uiUserController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    //================================================================================
    // GET Methods
    //================================================================================

    /**
     * Retrieves all users in the system
     *
     * @return List of all users or empty list if none exist
     */
    @GetMapping("")
    public String getAllUsers(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            if (user.getRole().equals("admin")) {
                List<User> users = userService.findAll();
                model.addAttribute("users", users);
                return "userList"; // userList.html
            }
            return "404";
        }
        return "404";
    }


    /**
     * Retrieves a specific user's profile by their ID
     *
     * @param id The unique identifier of the user
     * @return The requested user profile if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public String getUserProfileById(@PathVariable int id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Optional<User> userProfile = userService.findById(id);
            if (userProfile.isPresent()) {
                if (userProfile.get().getRole().equals("admin")) {
                    return "redirect:/admin";
                }
                model.addAttribute("user", userProfile.get());
                return "userProfile"; // userProfile.html
            }
            model.addAttribute("errorMessage", "User not found.");
            return "error";
        }
        return "redirect:/login";
    }

    /**
     * Retrieves favorite recipes for the currently logged-in user
     *
     * @param session The current HTTP session containing user information
     * @return List of favorite recipes if user is logged in, or 401 if not authenticated
     */
    @GetMapping("/favorites")
    public String getUserFavorites(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("favorites", user.getFavourites());
            return "favorites"; // favorites.html
        }
        model.addAttribute("errorMessage", "User not logged in.");
        return "error";
    }

    //================================================================================
    // POST Methods
    //================================================================================

    /**
     * Registers a new user in the system
     *
     * @param user The user object containing registration details
     * @return Success message if user created, error if user already exists
     */
    @PostMapping("/register")
    public String newUser(@ModelAttribute("user") User user, Model model) {
        Optional<User> existingUser = userService.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            model.addAttribute("errorMessage", "User already registered with this email.");
            return "register";
        }
        userService.save(user);
        return "redirect:/usersui"; // Redirects to users list after registration
    }

    /**
     * Authenticates a user and creates a session
     *
     * @param session The HTTP session to store user information
     * @param model   The user credentials for authentication
     * @return Success message if login successful, error if credentials invalid or user not found
     */

    // GET mapping for the user profile page
    @GetMapping("/profile")
    public String showUserProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            model.addAttribute("errorMessage", "User not logged in.");
            return "error";
        }
        model.addAttribute("user", user);
        return "userProfile";
    }


    //================================================================================
    // DELETE Methods
    //================================================================================

    /**
     * Removes a user from the system
     *
     * @param id The ID of the user to delete
     * @return Redirects to users list with success message
     */
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable int id, Model model) {
        userService.deleteById(id);
        model.addAttribute("message", "User deleted successfully");
        return "redirect:/usersui"; // Redirects to users list page
    }

    /**
     * Removes a recipe from the current user's favorites list.
     *
     * @param recipeId The ID of the recipe to remove from favorites
     * @param session  The HTTP session to identify the logged-in user
     * @return Success message if removed, 404 if recipe or user not found, or 401 if not logged in
     */
    @PostMapping("/favorites/{recipeId}/remove")
    public String removeFavoriteRecipe(@PathVariable int recipeId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            System.out.println("halló ekki user hér");
            model.addAttribute("errorMessage", "User not logged in.");
            return "error";
        }

        Optional<Recipe> recipeToRemove = user.getFavourites().stream()
                .filter(recipe -> recipe.getId() == recipeId)
                .findFirst();

        if (recipeToRemove.isPresent()) {
            user.getFavourites().remove(recipeToRemove.get());
            userService.save(user);
            return "redirect:/usersui/favorites"; // Redirects to favorites page
        }
        System.out.println("hæ hér er villan");
        model.addAttribute("errorMessage", "Recipe not found in favorites.");
        return "error";
    }

    //================================================================================
    // PATCH Methods
    //================================================================================

    /**
     * Shows the password change form for the logged-in user.
     *
     * @param id      The user's ID.
     * @param session The session to check if the user is logged in.
     * @param model   The model
     * @return Displays the password change page if the user is logged in
     */

    @GetMapping("/{id}/changepassword")
    public String showChangePasswordForm(@PathVariable int id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getId() != id) {
            model.addAttribute("errorMessage", "Unauthorized access.");
            return "redirect:/login";
        }
        if (user.getRole().equals("admin")) {
            model.addAttribute("isAdmin", true);
        } else model.addAttribute("isAdmin", false);
        model.addAttribute("user", user);
        return "changePassword";
    }

    /**
     * Handles password change for the logged-in user.
     *
     * @param id                 The user's ID.
     * @param session            The session to get the logged-in user info.
     * @param currentPassword    The user's current password.
     * @param newPassword        The new password to be set.
     * @param confirmNewPassword The new password retyped for confirmation.
     * @param model              The model to display messages.
     * @return Redirects to profile if successful, or shows errors on the same page.
     */

    @PostMapping("/{id}/changepassword")
    public String changePassword(@PathVariable int id,
                                 HttpSession session,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmNewPassword") String confirmNewPassword,
                                 Model model) {

        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        model.addAttribute("currentPassword", currentPassword);

        if (user == null || user.getId() != id) {
            System.out.println("halló ég er villan sem þú ert að leita af");
            model.addAttribute("errorMessage", "Unauthorized access.");
            return "404";
        }
        // check if current password matches
        if (!user.getPassword().equals(currentPassword)) {
            model.addAttribute("errorMessage", "Your password is incorrect");
            return "changePassword";
        }

        if (newPassword.isEmpty() && confirmNewPassword.isEmpty()) {
            model.addAttribute("pwMessage", "Please add a new password");
            return "changePassword";
        }

        if (confirmNewPassword.isEmpty()) {
            model.addAttribute("pwMessage", "Please repeat the new password");
            return "changePassword";
        }

        if (newPassword.isEmpty()) {
            model.addAttribute("pwMessage", "Please repeat the new password");
            return "changePassword";
        }

        if (user.getPassword().equals(newPassword)) {
            model.addAttribute("errorMessage", "The new password can't be the same as the old password");
            return "changePassword";
        }

        // Update the password
        user.setPassword(newPassword);
        userService.save(user);
        if (user.getRole().equals("admin")) {
            model.addAttribute("message", "Password changed successfully.");
            return "redirect:/admin";
        }
        return "redirect:/usersui/" + id; // redirect to profile page
    }

    @PostMapping("/{userId}/upload-profile-picture")
    public String uploadProfilePicture(@PathVariable int userId,
                                       @RequestParam("file") MultipartFile file,
                                       RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "No file selected.");
                return "redirect:/usersui/" + userId;
            }

            String imageUrl = imageService.uploadImage(file);
            user.setProfilePictureUrl(imageUrl);
            userService.save(user);

            redirectAttributes.addFlashAttribute("message", "Profile picture updated!");
            return "redirect:/usersui/" + userId;
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading image: " + e.getMessage());
            return "redirect:/usersui/" + userId;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/usersui/" + userId;
        }
    }
}


