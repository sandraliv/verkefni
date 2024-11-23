package com.hi.recipe.verkefni;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
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
        // Load environment variables from .env
        String currentDir = System.getProperty("user.dir");
        System.out.println("Current Working Directory: " + currentDir);
        Dotenv dotenv = Dotenv.load();

        // Initialize Cloudinary with CLOUDINARY_URL
        return new Cloudinary(dotenv.get("CLOUDINARY_URL"));
    }
}
