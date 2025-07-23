package com.indla.SpringMbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // returns login.html
    }
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

}
