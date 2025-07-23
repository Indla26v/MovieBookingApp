package com.indla.SpringMbooking.controller;

import com.indla.SpringMbooking.dto.TheatreDto;
import com.indla.SpringMbooking.service.TheatreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatres")
public class TheatreController {

    @Autowired
    private TheatreService theatreService;

    @GetMapping("/search")
    public List<TheatreDto> searchTheatres(@RequestParam String location) {
        return theatreService.searchTheatres(location);
    }
}
