package com.hi.recipe.verkefni.uiControllers;

import com.hi.recipe.verkefni.klasar.*;
import com.hi.recipe.verkefni.services.RecipeService;
import com.hi.recipe.verkefni.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class uiContactController {
    private final UserService userService;
    private final RecipeService recipeService;

    public uiContactController(UserService userService, RecipeService recipeService) {
        this.userService = userService;
        this.recipeService = recipeService;
    }

    @GetMapping("/error")
    public String handle404() {
        return "404";
    }

    /**
     * Displays the contact form page.
     *
     * @param model The model .
     * @return contact.html
     */
    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("contactForm", new ContactForm());
        return "contact"; // contact.html
    }

    /**
     * Processes the submitted contact form.
     *
     * @param contactForm        The contact form data submitted by the user.
     * @param result             checks for any mistakes in the form
     * @param redirectAttributes Used to pass a success message after redirect.
     * @param model              The model
     * @return Redirects to /contact with either with a success message or error if they are any.
     */

    @PostMapping("/contact")
    public String submitContactForm(@Valid @ModelAttribute("contactForm") ContactForm contactForm, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        // Process form data here, e.g., save to database or send an email
        // For this example, we'll just return a confirmation message
        if (result.hasErrors()) {
            // Return to form with errors
            return "contact";
        }

        // Add a success message to flash attributes
        redirectAttributes.addFlashAttribute("message", "Contact form submitted successfully!");

        // Redirect to the contact form to show an empty form
        return "redirect:/contact";
    }


    /**
     * Displays the login form page.
     *
     * @param model The model
     * @return login.html
     */
    @GetMapping("/login")
    public String showLoginForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return "redirect:/usersui/" + user.getId();
        }
        model.addAttribute("user", new User());
        return "login";
    }

    /**
     * Displays the login form page.
     *
     * @param model   The model
     * @param session user session
     * @return to login after invalidating a session
     */
    @GetMapping("")
    public String getFrontPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        model.addAttribute("recipes", recipeService.findAllPaginated());
        model.addAttribute("allTags", RecipeTag.values());
        return "frontPage";
    }

    @GetMapping("/logout")
    public String logUserOut(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            session.invalidate();
            return "redirect:/";
        }
        return "redirect:/login";
    }

    @GetMapping("/admin")
    public String getAdminView(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equals("admin")) {
            // Redirect to a different page if the user is not an admin
            return "redirect:/"; // Or any other page
        }
        model.addAttribute("user", user);
        return "admin"; // Or the appropriate admin page
    }


    /**
     * @param recipe New recipe object
     * @param model  The model
     * @return addRecipe.html
     */
    @GetMapping("/admin/addRecipe")
    public String addNewRecipe(@ModelAttribute("recipe") Recipe recipe, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if (user.getRole().equals("admin")) {
            model.addAttribute("newRecipe", new Recipe());
            model.addAttribute("allTags", RecipeTag.values()); // Pass all enum values
            model.addAttribute("allCategories", Category.values());
            return "addRecipe";
        }
        // Redirect to the home page or an appropriate page if the user is not an admin
        return "redirect:/";
    }

    /**
     * Creates a new recipe in the system
     *
     * @param recipe The recipe object containing all required recipe data
     * @return Redirects to recipe list with a success message
     */
    @PostMapping("/admin/newRecipe")
    public String addANewRecipe(@ModelAttribute("recipe") Recipe recipe, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model, HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("message", "Please fix the errors in the form.");
            return "addRecipe"; // Replace with the name of your form template
        }
        User user = (User) session.getAttribute("user");
        assert user != null;
        if (user.getRole().equals("admin")) {
            Recipe savedRecipe = recipeService.save(recipe);
            redirectAttributes.addAttribute("recipeId", savedRecipe.getId());
            return "redirect:/recipesui/{recipeId}/upload"; // Redirects to upload with recipeId
        }
        return "redirect:/error/404";
    }

    /**
     * Handles the login form submission.
     *
     * @param session Used to store info about the logged-in user.
     * @param user    The user’s login info (username and password).
     * @param model   The model
     * @return Goes to the user’s profile page if login works, or shows the login page with an error message if it doesn’t.
     */
    @PostMapping("/login")
    public String login(HttpSession session, @ModelAttribute("user") User user, Model model) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            model.addAttribute("errorMessage", "Please provide a username.");
            return "login";
        }
        Optional<User> optionalUser = userService.findByUsername(user.getUsername());
        if (optionalUser.isEmpty()) {
            model.addAttribute("errorMessage", "User not found.");
            return "login";
        }
        User foundUser = optionalUser.get();
        if (!foundUser.getPassword().equals(user.getPassword())) {
            model.addAttribute("errorMessage", "User not found.");
            return "login";
        }
        session.setAttribute("user", foundUser);
        return "redirect:/usersui/" + foundUser.getId();
    }
}
