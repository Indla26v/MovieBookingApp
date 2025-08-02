package com.indla.SpringMbooking.controller;

import com.indla.SpringMbooking.dto.ManagerRegistrationDto;
import com.indla.SpringMbooking.dto.ManagerUpdateDto;
import com.indla.SpringMbooking.dto.TheatreRegistrationDto;
import com.indla.SpringMbooking.dto.UserRegistrationDto;
import com.indla.SpringMbooking.model.Movie;
import com.indla.SpringMbooking.model.Showtime;
import com.indla.SpringMbooking.model.Theatre;
import com.indla.SpringMbooking.model.User;
import com.indla.SpringMbooking.repository.MovieRepository;
import com.indla.SpringMbooking.repository.ShowtimeRepository;
import com.indla.SpringMbooking.repository.TheatreRepository;
import com.indla.SpringMbooking.repository.UserRepository;
import com.indla.SpringMbooking.service.MovieService;
import com.indla.SpringMbooking.service.TheatreService;
import com.indla.SpringMbooking.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private TheatreRepository theatreRepo;
    @Autowired
    private MovieRepository movieRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private TheatreService theatreService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showAdminDashboard() {
        return "admin-dashboard";
    }

    // ===================== MOVIE MANAGEMENT =====================

    @GetMapping("/movies")
    public String showMovies(Model model) {
        model.addAttribute("movies", movieRepo.findAll());
        model.addAttribute("movie", new Movie());
        return "movie";
    }

    @PostMapping("/movies")
    public String addMovie(@ModelAttribute Movie movie) {
        movieRepo.save(movie);
        return "redirect:/admin/movies";
    }

    @PostMapping("/movies/{id}")
    public String deleteMovie(@PathVariable Long id) {
        movieRepo.deleteById(id);
        return "redirect:/admin/movies";
    }

    // ===================== USER MANAGEMENT =====================

    @GetMapping("/users")
    public String showUserManagement(Model model) {
        model.addAttribute("users", userRepo.findAll());
        return "user-management";
    }

    @GetMapping("/register/admin")
    public String showAdminRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "register-admin";
    }

    @PostMapping("/register/admin")
    public String registerAdmin(@ModelAttribute("userDto") UserRegistrationDto userDto, Model model, RedirectAttributes redirectAttributes) {
        if (userService.existsByUsernameOrEmail(userDto.getUsername(), userDto.getEmail())) {
            model.addAttribute("error", "Username or email already exists.");
            return "register-admin";
        }
        boolean success = userService.registerUser(userDto, "ROLE_ADMIN");
        if (success) {
            redirectAttributes.addFlashAttribute("message", "New admin registered successfully!");
            return "redirect:/admin/users";
        }
        return "register-admin";
    }

    @PostMapping("/users/{id}/update-role")
    public String updateUserRole(@PathVariable Long id, @RequestParam String role) {
        User user = userRepo.findById(id).orElseThrow();
        user.setRole(role);
        userRepo.save(user);
        return "redirect:/admin/users";
    }

    // ===================== THEATRE MANAGEMENT =====================
    @GetMapping("/theatres")
    public String showTheatreManagement(Model model) {
        model.addAttribute("theatres", theatreRepo.findAll());
        model.addAttribute("theatreDto", new TheatreRegistrationDto());
        return "theatre-management";
    }

    @PostMapping("/theatres")
    public String registerNewTheatre(@ModelAttribute TheatreRegistrationDto theatreDto, RedirectAttributes redirectAttributes) {
        Theatre theatre = new Theatre();
        theatre.setName(theatreDto.getName());
        theatre.setLocation(theatreDto.getLocation());
        theatre.setLatitude(theatreDto.getLatitude());
        theatre.setLongitude(theatreDto.getLongitude());
        theatreRepo.save(theatre);
        redirectAttributes.addFlashAttribute("message", "New theatre registered successfully!");
        return "redirect:/admin/theatres";
    }

    // ===================== MANAGER MANAGEMENT =====================
    @GetMapping("/managers")
    public String showManagerManagement(Model model) {
        List<User> managers = userRepo.findAll().stream()
                .filter(user -> "ROLE_MANAGER".equals(user.getRole()))
                .collect(Collectors.toList());

        // Prepare a map of managerId -> assigned theatres to use in the template
        Map<Long, List<Theatre>> managerTheatresMap = new HashMap<>();
        for (User manager : managers) {
            List<Theatre> assignedTheatres = theatreRepo.findByManager(manager);
            managerTheatresMap.put(manager.getId(), assignedTheatres);
        }

        model.addAttribute("managers", managers);
        model.addAttribute("managerTheatresMap", managerTheatresMap);
        model.addAttribute("allTheatres", theatreRepo.findAll());
        return "manager-management";
    }

    @PostMapping("/managers/update")
    public String updateManager(@ModelAttribute ManagerUpdateDto managerDto) {
        User manager = userRepo.findById(managerDto.getId()).orElseThrow();
        manager.setUsername(managerDto.getUsername());
        manager.setEmail(managerDto.getEmail());
        userRepo.save(manager);

        theatreService.assignTheatresToManager(manager.getId(), managerDto.getTheatreIds());

        return "redirect:/admin/managers";
    }
}
