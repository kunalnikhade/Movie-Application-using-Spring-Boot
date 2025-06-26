package com.movie.movieApp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto
{
    @NotNull(message = "please provide movie title")
    private String title;

    @NotNull(message = "please provide movie director")
    private String director;

    @NotNull(message = "please provide movie studio")
    private int studio;

    private int releaseYear;

    private Set<String> movieCast;

    @NotNull(message = "please provide movie poster")
    private String poster;

    @NotNull(message = "please provide posters url")
    private String posterUrl;
}
