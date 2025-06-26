package com.movie.movieApp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FileServiceImpl implements FileService
{
    @Override
    @Transactional
    public String uploadFile(String path, MultipartFile file) throws IOException
    {
        // Get the file name from the multipart file
        String fileName = file.getOriginalFilename();

        // Ensure the upload directory exists
        File uploadDir = new File(path);
        if (!uploadDir.exists())
        {
            uploadDir.mkdirs(); // Creates directory and any necessary parent directories
        }

        // Construct the full file path for the new file
        String filePath = path + File.separator + fileName;

        // Copy the file to the destination, replacing if it already exists
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }

    @Override
    @Transactional(readOnly = true)
    public InputStream getResourceFile(String path, String fileName) throws IOException
    {
        String filePath = path + File.separator + fileName;

        return new FileInputStream(filePath);
    }

}
