package com.movie.movieApp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.movieApp.dto.MovieDto;
import com.movie.movieApp.dto.MoviePageResponse;
import com.movie.movieApp.exceptions.EmptyFileException;
import com.movie.movieApp.service.MovieService;
import com.movie.movieApp.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/movie")
public class MovieController
{
    private final MovieService movieService;
    private final ObjectMapper objectMapper;

    @Autowired
    public MovieController(final MovieService movieService, final ObjectMapper objectMapper)
    {
        this.movieService = movieService;
        this.objectMapper = objectMapper;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PostMapping(value = "/add-movie", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file, @RequestPart String movieDtoJson) throws IOException, EmptyFileException
    {
        // Convert the JSON string to a MovieDto object
        MovieDto movieDto = objectMapper.readValue(movieDtoJson, MovieDto.class);

        if(file.isEmpty())
        {
            throw new EmptyFileException("File is empty! Please send another file!");
        }
        return new ResponseEntity<>(movieService.addMovie(movieDto, file), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{movieId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieDto> getMovieById(@PathVariable Integer movieId)
    {
        return new ResponseEntity<>(movieService.getMovieById(movieId), HttpStatus.OK);
    }

    @GetMapping(value = "/all-movies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MovieDto>> getAllMovies()
    {
        return new ResponseEntity<>(movieService.getAllMovies(), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{movieId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieDto> updateMovieById(@PathVariable Integer movieId, @RequestPart String movieDtoJson, @RequestPart MultipartFile file) throws IOException
    {
        // Convert the JSON string to a MovieDto object
        MovieDto movieDto = objectMapper.readValue(movieDtoJson, MovieDto.class);

        return ResponseEntity.ok(movieService.updateMovie(movieDto, file, movieId));
    }

    @DeleteMapping(value = "/delete/{movieId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MovieDto> deleteMovieById(@PathVariable Integer movieId)
    {
        movieService.deleteMovie(movieId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/allMoviePages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MoviePageResponse> getMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize)
    {
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber, pageSize));
    }

    @GetMapping(value = "/allMoviePages-Sort", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MoviePageResponse> getMoviesWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIRECTION, required = false) String direction)
    {
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber, pageSize, sortBy, direction));
    }
}
