package com.scm.movieApp.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface FileService
{
    // Saves the uploaded file to a specific directory.
    String uploadFile(String path, MultipartFile file) throws IOException;

    // Retrieves a file as an input stream.
    InputStream getResourceFile(String path, String fileName) throws IOException;
}
