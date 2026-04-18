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
import com.app.movieBookingSystem.dto.LoginRequest;
import com.app.movieBookingSystem.dto.JwtResponse;
import com.app.movieBookingSystem.model.Movie;
import com.app.movieBookingSystem.model.User;
import com.app.movieBookingSystem.repository.UserRepository;
import com.app.movieBookingSystem.service.MovieService;
import com.app.movieBookingSystem.security.JwtUtils;
import com.app.movieBookingSystem.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/v1.0/moviebooking")
public class MovieController {

    @Autowired
    private MovieService movieService;
    @Autowired
    private UserRepository userRepo;


    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        java.util.Optional<User> userOpt = userRepo.findByLoginId(loginRequest.getLoginId());
        
        if (userOpt.isPresent() && encoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            User user = userOpt.get();
            String jwt = jwtUtils.generateJwtToken(user.getLoginId());
            String role = user.getRole() != null ? user.getRole() : "ROLE_USER";
            
            return ResponseEntity.ok(new JwtResponse(jwt,
                    user.getId(),
                    user.getLoginId(),
                    user.getEmail(),
                    role));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid credentials");
        }
    }

    // usercontroller
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            // Ensure ID is null so JPA knows it's a new entity and not an update
            user.setId(null);
            
            if (user.getPassword() != null) {
                user.setPassword(encoder.encode(user.getPassword()));
            }
            userRepo.save(user);
            return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            return new ResponseEntity<>("Error: Email or Login ID already exists!", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: Unable to register user. Reason: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    public ResponseEntity<?> addMovie(@RequestBody MovieRequest movieRequest) {
        if(movieRequest.getTotalTickets()>100){
            return new ResponseEntity<>("Hundred Seats only allowed ",HttpStatus.NOT_ACCEPTABLE);
        }
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