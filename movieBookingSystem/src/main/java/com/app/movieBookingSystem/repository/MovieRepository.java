package com.app.movieBookingSystem.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.app.movieBookingSystem.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, String> {
    List<Movie> findByMovieNameContaining(String movieName);
    Movie findByMovieName(String movieName);
}