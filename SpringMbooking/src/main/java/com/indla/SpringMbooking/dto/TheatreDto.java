package com.indla.SpringMbooking.dto;

public class TheatreDto {

    private Long id;
    private String name;
    private String location;
    private String currentMovie;
    private Double rating;
    private String nextShowtime;

    public TheatreDto() {}

    public TheatreDto(Long id, String name, String location, String currentMovie, Double rating, String nextShowtime) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.currentMovie = currentMovie;
        this.rating = rating;
        this.nextShowtime = nextShowtime;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCurrentMovie() {
        return currentMovie;
    }

    public void setCurrentMovie(String currentMovie) {
        this.currentMovie = currentMovie;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getNextShowtime() {
        return nextShowtime;
    }

    public void setNextShowtime(String nextShowtime) {
        this.nextShowtime = nextShowtime;
    }
}
