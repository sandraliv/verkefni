package com.hi.recipe.verkefni;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class VerkefniApplication {

    @Value("${CLOUDINARY_URL}")
    private String cloudinaryUrl;

    public static void main(String[] args) {
        SpringApplication.run(VerkefniApplication.class, args);
    }

    // Configure Cloudinary Bean with dotenv
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(cloudinaryUrl);
    }
}
