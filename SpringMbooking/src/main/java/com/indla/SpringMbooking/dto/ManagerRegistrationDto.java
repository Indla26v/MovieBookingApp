package com.indla.SpringMbooking.dto;

import java.util.List;

public class ManagerRegistrationDto {

    private String username;
    private String email;
    private String password;
    private List<Long> theatreIds; // IDs of theatres this manager will manage

    public ManagerRegistrationDto() {}

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Long> getTheatreIds() {
        return theatreIds;
    }

    public void setTheatreIds(List<Long> theatreIds) {
        this.theatreIds = theatreIds;
    }
}