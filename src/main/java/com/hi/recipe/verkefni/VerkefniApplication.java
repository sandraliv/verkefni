package com.hi.recipe.verkefni;

import com.cloudinary.Cloudinary;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class VerkefniApplication {

    public static void main(String[] args) {
        SpringApplication.run(VerkefniApplication.class, args);
    }

    // Configure Cloudinary Bean with dotenv
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(System.getenv("CLOUDINARY_URL"));
    }
}
