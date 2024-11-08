package com.hi.recipe.verkefni.uiControllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.hi.recipe.verkefni.klasar.ContactForm;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/ui")
public class uiContactController {

    @GetMapping("contact")
    public String showContactForm(Model model) {
        model.addAttribute("contactForm", new ContactForm()); 
        return "contact"; // shows the contact.html page
    }

    @PostMapping("/contact_us")
    public String submitContactForm(@Valid @ModelAttribute("contactForm") ContactForm contactForm, Model model) {
         // Process form data here, e.g., save to database or send an email
        // For this example, we'll just return a confirmation message
        
        model.addAttribute("message", "Contact form submitted successfully!");
 
        return "contact"; 
}

}
