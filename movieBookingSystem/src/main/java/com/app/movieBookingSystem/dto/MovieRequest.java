package com.app.movieBookingSystem.dto;

public class MovieRequest {

    private String movieName;
    private String theatreName;
    private Integer totalTickets;
    private String ticketStatus;
    
    public String getMovieName() {
        return movieName;
    }
    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
    public String getTheatreName() {
        return theatreName;
    }
    public void setTheatreName(String theatreName) {
        this.theatreName = theatreName;
    }
    public Integer getTotalTickets() {
        return totalTickets;
    }
    public void setTotalTickets(Integer totalTickets) {
        this.totalTickets = totalTickets;
    }
    public String getTicketStatus() {
        return ticketStatus;
    }
    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    
    
}
