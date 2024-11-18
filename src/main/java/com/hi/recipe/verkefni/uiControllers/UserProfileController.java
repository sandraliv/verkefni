package com.hi.recipe.verkefni.uiControllers;

import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.services.ImageService;
import com.hi.recipe.verkefni.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/usersui")
public class UserProfileController {

    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public UserProfileController(UserService userService, ImageService imageService) {
        this.userService = userService;
        this.imageService = imageService;
    }

    
    @GetMapping("/{userId}/profile")
    public String showUserProfile(@PathVariable int userId, Model model) {
        
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        model.addAttribute("user", user); 
        return "userProfile"; 
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
                return "redirect:/usersui/" + userId + "/profile";
            }

            
            String imageUrl = imageService.uploadImage(file);

            
            user.setProfilePictureUrl(imageUrl);
            userService.save(user);

            redirectAttributes.addFlashAttribute("message", "Profile picture updated!");
            return "redirect:/usersui/" + userId + "/profile";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading image: " + e.getMessage());
            return "redirect:/usersui/" + userId + "/profile";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/usersui/" + userId + "/profile";
        }
    }
}
