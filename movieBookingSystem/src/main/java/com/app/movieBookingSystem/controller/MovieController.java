package com.app.movieBookingSystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.app.movieBookingSystem.dto.MovieRequest;
import com.app.movieBookingSystem.dto.TicketRequest;
import com.app.movieBookingSystem.model.Movie;
import com.app.movieBookingSystem.model.User;
import com.app.movieBookingSystem.repository.UserRepository;
import com.app.movieBookingSystem.service.MovieService;

@RestController
@RequestMapping("/api/v1.0/moviebooking")
public class MovieController {

    @Autowired
    private MovieService movieService;
    @Autowired
    private UserRepository userRepo;

    // usercontroller
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        userRepo.save(user);
        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        if (movies.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @PostMapping("/addMovie")
    public ResponseEntity<Movie> addMovie(@RequestBody MovieRequest movieRequest) {
        Movie movie = movieService.addMovie(movieRequest);
        return new ResponseEntity<>(movie, HttpStatus.CREATED);
    }

    @GetMapping("/movies/search/{moviename}")
    public ResponseEntity<List<Movie>> searchMovie(@PathVariable String moviename) {
        List<Movie> movies = movieService.searchMovie(moviename);
        if (movies.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    // ticketcontroller
    @PostMapping("/{moviename}/book")
    public ResponseEntity<String> bookTicket(@PathVariable String moviename, @RequestBody TicketRequest ticketRequest) {
        String message = movieService.bookTicket(moviename, ticketRequest);
        // You could add logic here to check the message content and return 400 if failed
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping("/{moviename}/delete/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable String moviename, @PathVariable String id) {
        boolean isDeleted = movieService.deleteMovie(moviename, id);
        if (isDeleted) {
            return new ResponseEntity<>("Movie deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Movie not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/booked-seats-text/{movieName}")
    public ResponseEntity<String> getBookedSeatsText(@PathVariable String movieName) {
        String allSeats = movieService.getBookedSeatsString(movieName);
        if (allSeats.isEmpty()) {
            return ResponseEntity.ok("No seats found for: " + movieName);
        }
        return ResponseEntity.ok(allSeats);
    }

    @DeleteMapping("/tickets/delete-all")
    public ResponseEntity<String> deleteAllTickets() {
        movieService.deleteAllTickets();
        return ResponseEntity.ok("All tickets have been deleted successfully.");
    }

    @DeleteMapping("/movies/delete-all")
    public ResponseEntity<String> deleteAllMovies() {
        movieService.deleteAllMovies();
        return ResponseEntity.ok("All movies have been deleted successfully.");
    }
}