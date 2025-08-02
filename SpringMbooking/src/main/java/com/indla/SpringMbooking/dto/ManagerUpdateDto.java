package com.indla.SpringMbooking.dto;

import java.util.List;

public class ManagerUpdateDto {

    private Long id;
    private String username;
    private String email;
    private List<Long> theatreIds; // IDs of theatres this manager will manage

    public ManagerUpdateDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Long> getTheatreIds() {
        return theatreIds;
    }

    public void setTheatreIds(List<Long> theatreIds) {
        this.theatreIds = theatreIds;
    }
}
