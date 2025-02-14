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
        Dotenv dotenv = Dotenv.load();
        return new Cloudinary(dotenv.get("CLOUDINARY_URL"));
    }
}
