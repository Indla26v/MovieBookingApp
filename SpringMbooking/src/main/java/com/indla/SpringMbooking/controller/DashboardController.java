package com.indla.SpringMbooking.controller;

import com.indla.SpringMbooking.dto.TheatreDto;
import com.indla.SpringMbooking.service.TheatreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @Autowired
    private TheatreService theatreService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("username", username);

        List<TheatreDto> theatres = theatreService.getNearbyTheatres(); // or getAllTheatres()
        model.addAttribute("theatres", theatres);

        return "dashboard"; // this maps to dashboard.html
    }
}
