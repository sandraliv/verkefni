package com.hi.recipe.verkefni.klasar;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "contact_message")
public class ContactForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    @NotBlank(message = "Please enter a name") // Changed from @NotNull to @NotBlank
    private String name;

    @Column(name = "email")
    @Email(message = "Setja þarf inn netfang svo við getum haft samband")
    @NotBlank(message = "Please add email address") // Add @NotBlank to enforce non-empty
    private String email;

    @Column(name = "subject")
    @NotBlank(message = "Please add subject title")
    private String subject;

    @Column(name = "message")
    @Size(min = 10, message = "Message is too short")
    @NotBlank(message = "Message can't be empty") // Ensure the message isn't blank
    private String message;

    public ContactForm() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
