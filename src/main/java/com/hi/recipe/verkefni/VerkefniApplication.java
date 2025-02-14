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

//    // Configure Cloudinary Bean with dotenv
//    @Bean
//    public Cloudinary cloudinary() {
//        // Load environment variables from .env
//        String currentDir = System.getProperty("user.dir");
//        System.out.println("Current Working Directory: " + currentDir);
//        Dotenv dotenv = Dotenv.load();
//
//
//        // Initialize Cloudinary with CLOUDINARY_URL
//        return new Cloudinary(dotenv.get("CLOUDINARY_URL"));
//    }
    @Bean
    public Cloudinary cloudinary(@Value("${CLOUDINARY_URL}") String cloudinaryUrlFromEnv) {
        String cloudinaryUrl = cloudinaryUrlFromEnv;
        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            // Fallback to Dotenv for local development
            Dotenv dotenv = Dotenv.configure()
                    .directory(System.getProperty("user.dir"))
                    .ignoreIfMissing()
                    .load();
            cloudinaryUrl = dotenv.get("CLOUDINARY_URL");
        }

        if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
            throw new IllegalStateException("CLOUDINARY_URL is not set");
        }
        return new Cloudinary(cloudinaryUrl);
    }


}
