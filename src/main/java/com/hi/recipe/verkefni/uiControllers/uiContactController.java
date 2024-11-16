package com.hi.recipe.verkefni.uiControllers;

import com.hi.recipe.verkefni.klasar.ContactForm;
import com.hi.recipe.verkefni.klasar.RecipeTag;
import com.hi.recipe.verkefni.klasar.User;
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
