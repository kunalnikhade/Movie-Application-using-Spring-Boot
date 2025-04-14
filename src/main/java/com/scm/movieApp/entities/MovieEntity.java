package com.scm.movieApp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "Movie")
public class MovieEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "title", nullable = false, length = 200)
    @NotNull(message = "please provide movie title")
    private String title;

    @Column(name = "director", nullable = false)
    @NotNull(message = "please provide movie director")
    private String director;

    @Column(name = "studio", nullable = false)
    @NotNull(message = "please provide movie studio")
    private int studio;

    @Column(name = "release_year")
    private int releaseYear;

    @ElementCollection
    @CollectionTable(name = "movie_cast", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "cast_member")
    private Set<String> movieCast;

    @Column(name = "poster", nullable = false)
    @NotNull(message = "please provide movie poster")
    private String poster;
}
