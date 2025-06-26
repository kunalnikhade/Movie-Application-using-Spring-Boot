package com.movie.movieApp.controllers;

import com.movie.movieApp.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file/")
public class FileController
{
    @Value("${file.upload-dir}")
    private String path;

    private final FileService fileService;

    @Autowired
    public FileController(final FileService fileService)
    {
        this.fileService = fileService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> uploadFileHandler(@RequestPart MultipartFile file) throws IOException
    {
        String uploadedFileName = fileService.uploadFile(path, file);

        return ResponseEntity.ok("Uploaded file: " + uploadedFileName);
    }

    @GetMapping(value = "/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getFileHandler(@PathVariable String fileName, HttpServletResponse response) throws IOException
    {
        InputStream resourceFile = fileService.getResourceFile(path, fileName);

        response.setContentType(MediaType.IMAGE_PNG_VALUE);

        StreamUtils.copy(resourceFile, response.getOutputStream());
    }
}
