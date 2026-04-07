package com.app.movieBookingSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.movieBookingSystem.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByMovieName(String movieName);
    List<Ticket> findByMovieNameIgnoreCase(String movieName);
}

