package com.scm.movieApp.repository;

import com.scm.movieApp.entities.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<MovieEntity, Integer>
{

}
