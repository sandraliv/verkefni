package com.hi.recipe.verkefni.uiControllers;
import com.hi.recipe.verkefni.klasar.User;
import com.hi.recipe.verkefni.services.UserService;
import com.hi.recipe.verkefni.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.hi.recipe.verkefni.klasar.ContactForm;

import jakarta.validation.Valid;

import java.util.Optional;

@Controller
public class uiContactController {
    private final UserService userService;

    public uiContactController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("contactForm", new ContactForm()); 
        return "contact"; // shows the contact.html page
    }

    @PostMapping("/contact")
    public String submitContactForm(@Valid @ModelAttribute("contactForm") ContactForm contactForm, BindingResult result,RedirectAttributes redirectAttributes, Model model) {
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

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String login(HttpSession session, @ModelAttribute("user") User user, Model model) {
        Optional<User> ou = userService.findByUsername(user.getUsername());
        if (ou.isPresent()) {
            User u = ou.get();
            if (u.getPassword().equals(user.getPassword())) {
                session.setAttribute("user", u);
                return "redirect:/usersui/" + u.getId();
            }
            model.addAttribute("errorMessage", "Notandi finnst ekki.");
            return "login";
        }
        model.addAttribute("errorMessage", "Notandi finnst ekki.");
        return "login";
    }
}
