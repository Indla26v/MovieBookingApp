package com.indla.SpringMbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index"; // corresponds to index.html in templates folder
    }

    @GetMapping("/login") // Add this to serve your login.html page
    public String loginPage() {
        return "login"; // returns login.html from src/main/resources/templates/
    }

    @GetMapping("/register") // Add this to serve your main user registration form
    public String showRegisterForm() {
        return "register"; // returns register.html from src/main/resources/templates/
    }
}