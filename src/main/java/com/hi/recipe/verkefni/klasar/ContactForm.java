package com.hi.recipe.verkefni.klasar;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="contact_message")
public class ContactForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    @NotNull(message = "Setja þarf inn nafn")
    private String name;

    @Column(name = "email")
    @Email(message = "Setja þarf inn netfang svo við getum haft samband")
    private String email;

    @Column(name = "subject")
    @NotBlank(message = "Setja þarf inn erindi")
    private String subject;

    @Column(name = "message")
    @Size(min = 10, message = "Skilaboðið má ekki vera færri en 10 stafir")
    private String message;

    public ContactForm (){

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
