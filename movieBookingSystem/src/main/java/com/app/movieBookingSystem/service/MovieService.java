package com.app.movieBookingSystem.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.movieBookingSystem.dto.MovieRequest;
import com.app.movieBookingSystem.dto.TicketRequest;
import com.app.movieBookingSystem.model.Movie;
import com.app.movieBookingSystem.model.Ticket;
import com.app.movieBookingSystem.repository.MovieRepository;
import com.app.movieBookingSystem.repository.TicketRepository;

import jakarta.transaction.Transactional;

@Service
public class MovieService {

    // Container for 100 valid tickets based on SeatReference (Rows A-J)
    public static final List<String> VALID_SEATS = new ArrayList<>();

    static {
        // Generates exactly 100 seat positions (e.g., A1, A2... J10)
        char[] rows = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'};
        for (char row : rows) {
            for (int i = 1; i <= 10; i++) {
                VALID_SEATS.add(row + String.valueOf(i));
            }
        }
    }

    @Autowired
    private MovieRepository movieRepo;
    @Autowired
    private TicketRepository ticketRepo;

    public String updateTicketStatus(String movieName) {
        // 1. Find the movie details (Total capacity)
        Movie movie = movieRepo.findByMovieName(movieName);
        int totalCapacity = 100;
        if (movie == null) {
            return "Movie not found";
        }

        // 2. Fetch ALL tickets associated with this movie
        List<Ticket> allTickets = ticketRepo.findByMovieName(movieName);

        // 3. Sum the 'numberOfTickets' from every ticket in the list
        // This handles 0, 1, or multiple tickets correctly
        int totalBooked = allTickets.stream()
                .mapToInt(Ticket::getNumberOfTickets)
                .sum();

        // 4. Calculate remaining capacity
        // Note: Use a separate variable or field for 'initialCapacity' if
        // 'totalTickets'
        // in your DB is being overwritten, otherwise the math will fail on the next
        // update.
        int available = totalCapacity - totalBooked;

        // 5. Update Status
        if (available <= 0) {
            movie.setTotalTickets(0); // Don't allow negative numbers
            movie.setTicketStatus("SOLD OUT");
        } else {
            movie.setTotalTickets(available);
            movie.setTicketStatus("BOOK ASAP");
        }

        movieRepo.save(movie);
        return "Status updated. Total booked: " + totalBooked + ", Available: " + available;
    }

    public List<Movie> getAllMovies() {
        return movieRepo.findAll();
    }

    public List<Movie> searchMovie(String moviename) {
        return movieRepo.findByMovieNameContaining(moviename);
    }

    public String bookTicket(String moviename, TicketRequest ticketRequest) {
        String inputSeats = ticketRequest.getSeatNumbers();
        if (inputSeats == null || inputSeats.isEmpty()) {
            return "No seats provided";
        }

        // Parse and standardize requested seats
        List<String> requestedSeats = Arrays.stream(inputSeats.split(",\\s*"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        // 0. Check if the provided seat names length matches the numberOfTickets
        if (requestedSeats.size() != ticketRequest.getNumberOfTickets()) {
            return String.format("Validation failed: You requested %d tickets, but provided %d seat names.", 
                    ticketRequest.getNumberOfTickets(), requestedSeats.size());
        }

        // 1. Validate if requested seats exist in our 100 valid seats container
        for (String seat : requestedSeats) {
            if (!VALID_SEATS.contains(seat)) {
                return "Validation failed: Invalid seat position '" + seat + "'. Valid seats are A1 to J10.";
            }
        }

        // 2. Validate against previously booked tickets for this movie
        String bookedSeatsStr = getBookedSeatsString(moviename);
        List<String> alreadyBooked = Arrays.asList(bookedSeatsStr.split(",\\s*"));

        for (String seat : requestedSeats) {
            if (alreadyBooked.contains(seat) && !seat.isEmpty()) {
                return "Booking failed: Seat '" + seat + "' is already booked for this movie.";
            }
        }

        Ticket newTicket = new Ticket();
        newTicket.setMovieName(moviename);
        newTicket.setTheatreName(ticketRequest.getTheatreName());
        newTicket.setNumberOfTickets(ticketRequest.getNumberOfTickets());
        newTicket.setSeatNumbers(String.join(", ", requestedSeats)); // Save standardized uniform string
        ticketRepo.save(newTicket);
        
        // Trigger status update logic
        updateTicketStatus(moviename);
        return "Tickets booked successfully";
    }

    public Movie addMovie(MovieRequest movieRequest) {
        Movie newMovie = new Movie();
        newMovie.setMovieName(movieRequest.getMovieName());
        newMovie.setTheatreName(movieRequest.getTheatreName());
        newMovie.setTotalTickets(movieRequest.getTotalTickets());
        newMovie.setTicketStatus(movieRequest.getTicketStatus());
        return movieRepo.save(newMovie);
    }

    @Transactional
    public boolean deleteMovie(String movieName, String movieId) {
        // 1. Find the movie
        Optional<Movie> movie = movieRepo.findById(movieId);

        if (movie.isPresent()) {
            // Check if the provided movieName matches the actual movie's name
            if (!movie.get().getMovieName().equalsIgnoreCase(movieName)) {
                return false;
            }

            // 2. Find all tickets as a List (Correct way to avoid ClassCastException)
            List<Ticket> tickets = ticketRepo.findByMovieName(movieName);

            // 3. Delete them if the list isn't empty
            if (tickets != null && !tickets.isEmpty()) {
                ticketRepo.deleteAll(tickets);
            }

            // 4. Finally delete the movie
            movieRepo.deleteById(movieId);
            return true;
        }
        return false;
    }

    public String getBookedSeatsString(String movieName) {
        // Use the IgnoreCase version to avoid 404s due to typing
        List<Ticket> tickets = ticketRepo.findByMovieNameIgnoreCase(movieName);

        if (tickets == null || tickets.isEmpty()) {
            return "";
        }

        return tickets.stream()
                .map(Ticket::getSeatNumbers)
                .filter(Objects::nonNull)
                .flatMap(seats -> Arrays.stream(seats.split(",\\s*")))
                .distinct()
                .collect(Collectors.joining(", "));
    }

    @Transactional
    public void deleteAllTickets() {
        ticketRepo.deleteAll();
    }

    @Transactional
    public void deleteAllMovies() {
        movieRepo.deleteAll();
    }
}