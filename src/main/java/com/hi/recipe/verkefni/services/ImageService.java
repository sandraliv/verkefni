package com.hi.recipe.verkefni.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {

    /**
     * Uploads an image and returns its URL.
     *
     * @param file the image file to upload
     * @return the URL of the uploaded image
     * @throws IOException if an I/O error occurs
     */
    String uploadImage(MultipartFile file) throws IOException;
}