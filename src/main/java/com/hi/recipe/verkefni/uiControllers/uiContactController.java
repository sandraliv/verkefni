package com.hi.recipe.verkefni.uiControllers;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.hi.recipe.verkefni.klasar.ContactForm;

import jakarta.validation.Valid;

@Controller
public class uiContactController {

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

}
