package com.indla.SpringMbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping("/dashboard")
    public String showManagerDashboard(Model model, Principal principal) {
        // You can add objects to the model here if the dashboard needs data
        // For example, fetching quick stats from a service
        // model.addAttribute("username", principal.getName());
        return "manager-dashboard"; // This returns the manager-dashboard.html file
    }
}