package com.indla.SpringMbooking.controller;

import com.indla.SpringMbooking.dto.UserRegistrationDto;
import com.indla.SpringMbooking.model.Movie;
import com.indla.SpringMbooking.model.Showtime;
import com.indla.SpringMbooking.model.Theatre;
import com.indla.SpringMbooking.model.User;
import com.indla.SpringMbooking.repository.MovieRepository;
import com.indla.SpringMbooking.repository.ShowtimeRepository;
import com.indla.SpringMbooking.repository.TheatreRepository;
import com.indla.SpringMbooking.repository.UserRepository;
import com.indla.SpringMbooking.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MovieRepository movieRepo;

    @Autowired
    private TheatreRepository theatreRepo;

    @Autowired
    private ShowtimeRepository showtimeRepo;

    @Autowired
    private UserRepository userRepository;

    // ===================== USER REGISTRATION =====================

    @PostMapping("/register")
    public String registerUser(@ModelAttribute UserRegistrationDto dto) {
        boolean success = userService.registerUser(dto, "USER");
        return success ? "redirect:/login" : "register";
    }


    // ===================== MANAGER REGISTRATION =====================

    @GetMapping("/register/manager")
    public String showManagerRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "register-manager";
    }

    @PostMapping("/register/manager")
    public String registerManager(@ModelAttribute("userDto") UserRegistrationDto userDto, Model model) {
        if (userService.existsByUsernameOrEmail(userDto.getUsername(), userDto.getEmail())) {
            model.addAttribute("error", "Username or email already exists.");
            return "register-manager";
        }

        User manager = new User();
        manager.setUsername(userDto.getUsername());
        manager.setEmail(userDto.getEmail());
        manager.setPassword(passwordEncoder.encode(userDto.getPassword()));
        manager.setRole("MANAGER");
        manager.setCreatedAt(LocalDateTime.now());

        userService.save(manager);
        return "redirect:/login?manager_registered";
    }

    // ===================== ADMIN - MOVIE MANAGEMENT =====================

    @GetMapping("/admin/movies")
    public String showMovies(Model model) {
        model.addAttribute("movies", movieRepo.findAll());
        return "movie";
    }

    @PostMapping("/admin/movies")
    public String addMovie(@ModelAttribute Movie movie) {
        movieRepo.save(movie);
        return "redirect:/auth/admin/movies";
    }

    @PostMapping("/admin/movies/{id}")
    public String deleteMovie(@PathVariable Long id) {
        movieRepo.deleteById(id);
        return "redirect:/auth/admin/movies";
    }

    // ===================== MANAGER - ASSIGN MOVIE TO THEATRE =====================

    @GetMapping("/manager/assign")
    public String showAssignForm(Model model, Principal principal) {
        String username = principal.getName();
        User manager = userRepository.findByUsername(username).orElseThrow();

        List<Theatre> theatres = theatreRepo.findByManager(manager);
        List<Movie> movies = movieRepo.findAll();

        model.addAttribute("movies", movies);
        model.addAttribute("theatres", theatres);
        model.addAttribute("singleTheatre", theatres.size() == 1);

        if (theatres.size() == 1) {
            model.addAttribute("theatreId", theatres.get(0).getId());
        }

        return "assign-movie";
    }

    @PostMapping("/manager/assign")
    public String assignMovie(
            @RequestParam Long movieId,
            @RequestParam Long theatreId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime showtime) {

        Movie movie = movieRepo.findById(movieId).orElseThrow();
        Theatre theatre = theatreRepo.findById(theatreId).orElseThrow();

        Showtime s = new Showtime();
        s.setMovie(movie);
        s.setTheatre(theatre);
        s.setShowtime(showtime);

        showtimeRepo.save(s);
        return "redirect:/auth/manager/assign?success";
    }
}
