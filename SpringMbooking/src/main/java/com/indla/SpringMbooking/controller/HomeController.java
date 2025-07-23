package com.indla.SpringMbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index.html";  // corresponds to index.html in templates folder
    }
}
