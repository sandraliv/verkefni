package com.hi.recipe.verkefni;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class VerkefniApplication {

    public static void main(String[] args) {
        SpringApplication.run(VerkefniApplication.class, args);
    }

    @Value("${CLOUDINARY_URL}")
    private String cloudinaryUrl;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(cloudinaryUrl);
    }
}
