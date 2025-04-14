package com.scm.movieApp.service;

import com.scm.movieApp.dto.MovieDto;
import com.scm.movieApp.dto.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService
{
    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException;

    MovieDto getMovieById(Integer movieId);

    List<MovieDto> getAllMovies();

    MovieDto updateMovie(MovieDto movieDto, MultipartFile file, Integer movieId) throws IOException;

    void deleteMovie(Integer movieId);

    MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);

    MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                           String sortBy, String direction);
}
