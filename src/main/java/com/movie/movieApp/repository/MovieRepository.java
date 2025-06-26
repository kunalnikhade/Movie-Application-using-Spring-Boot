package com.movie.movieApp.repository;

import com.movie.movieApp.entities.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<MovieEntity, Integer>
{

}
