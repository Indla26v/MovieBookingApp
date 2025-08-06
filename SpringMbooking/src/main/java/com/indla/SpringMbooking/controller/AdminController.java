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
    private ShowtimeRepository showtimeRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private MovieService movieService;
    @Autowired
    private TheatreService theatreService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showAdminDashboard(Model model) {
        // Add dashboard statistics
        model.addAttribute("totalUsers", userRepo.count());
        model.addAttribute("totalMovies", movieRepo.count());
        model.addAttribute("totalTheatres", theatreRepo.count());
        model.addAttribute("totalManagers", userRepo.findAll().stream()
                .filter(user -> "ROLE_MANAGER".equals(user.getRole()))
                .count());
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
    public String addMovie(@ModelAttribute Movie movie, RedirectAttributes redirectAttributes) {
        try {
            movieRepo.save(movie);
            redirectAttributes.addFlashAttribute("success", "Movie added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding movie: " + e.getMessage());
        }
        return "redirect:/admin/movies";
    }

    @PostMapping("/movies/{id}/delete")
    public String deleteMovie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Check if movie has associated showtimes
            Movie movie = movieRepo.findById(id).orElse(null);
            if (movie != null) {
                List<Showtime> showtimes = showtimeRepo.findByMovie(movie);
                if (!showtimes.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Cannot delete movie with existing showtimes!");
                    return "redirect:/admin/movies";
                }
                movieRepo.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Movie deleted successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting movie: " + e.getMessage());
        }
        return "redirect:/admin/movies";
    }

    @PostMapping("/movies/{id}/update")
    public String updateMovie(@PathVariable Long id, @ModelAttribute Movie movieUpdate,
                              RedirectAttributes redirectAttributes) {
        try {
            Movie movie = movieRepo.findById(id).orElse(null);
            if (movie != null) {
                movie.setTitle(movieUpdate.getTitle());
                movie.setLanguage(movieUpdate.getLanguage());
                movie.setRating(movieUpdate.getRating());
                movieRepo.save(movie);
                redirectAttributes.addFlashAttribute("success", "Movie updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating movie: " + e.getMessage());
        }
        return "redirect:/admin/movies";
    }

    // ===================== USER MANAGEMENT =====================

    @GetMapping("/users")
    public String showUserManagement(Model model) {
        List<User> users = userRepo.findAll();
        model.addAttribute("users", users);
        model.addAttribute("userStats", getUserStats(users));
        return "user-management";
    }

    private Map<String, Long> getUserStats(List<User> users) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", (long) users.size());
        stats.put("admins", users.stream().filter(u -> "ROLE_ADMIN".equals(u.getRole())).count());
        stats.put("managers", users.stream().filter(u -> "ROLE_MANAGER".equals(u.getRole())).count());
        stats.put("regularUsers", users.stream().filter(u -> "ROLE_USER".equals(u.getRole())).count());
        return stats;
    }

    @GetMapping("/register/admin")
    public String showAdminRegistrationForm(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "register-admin";
    }

    @PostMapping("/register/admin")
    public String registerAdmin(@ModelAttribute("userDto") UserRegistrationDto userDto,
                                Model model, RedirectAttributes redirectAttributes) {
        if (userService.existsByUsernameOrEmail(userDto.getUsername(), userDto.getEmail())) {
            model.addAttribute("error", "Username or email already exists.");
            return "register-admin";
        }
        boolean success = userService.registerUser(userDto, "ROLE_ADMIN");
        if (success) {
            redirectAttributes.addFlashAttribute("success", "New admin registered successfully!");
            return "redirect:/admin/users";
        }
        model.addAttribute("error", "Registration failed. Please try again.");
        return "register-admin";
    }

    @PostMapping("/users/{id}/update-role")
    public String updateUserRole(@PathVariable Long id, @RequestParam String role,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userRepo.findById(id).orElse(null);
            if (user != null) {
                user.setRole(role);
                userRepo.save(user);
                redirectAttributes.addFlashAttribute("success", "User role updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating user role: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userRepo.findById(id).orElse(null);
            if (user != null) {
                // Check if user is managing any theatres
                List<Theatre> managedTheatres = theatreRepo.findByManager(user);
                if (!managedTheatres.isEmpty()) {
                    // Unassign theatres before deletion
                    for (Theatre theatre : managedTheatres) {
                        theatre.setManager(null);
                        theatreRepo.save(theatre);
                    }
                }
                userRepo.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ===================== THEATRE MANAGEMENT =====================
    @GetMapping("/theatres")
    public String showTheatreManagement(Model model) {
        model.addAttribute("theatres", theatreRepo.findAll());
        model.addAttribute("theatreDto", new TheatreRegistrationDto());
        model.addAttribute("availableManagers", userRepo.findAll().stream()
                .filter(user -> "ROLE_MANAGER".equals(user.getRole()))
                .collect(Collectors.toList()));
        return "theatre-management";
    }

    @PostMapping("/theatres")
    public String registerNewTheatre(@ModelAttribute TheatreRegistrationDto theatreDto,
                                     RedirectAttributes redirectAttributes) {
        try {
            Theatre theatre = new Theatre();
            theatre.setName(theatreDto.getName());
            theatre.setLocation(theatreDto.getLocation());
            theatre.setLatitude(theatreDto.getLatitude());
            theatre.setLongitude(theatreDto.getLongitude());
            theatreRepo.save(theatre);
            redirectAttributes.addFlashAttribute("success", "New theatre registered successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error registering theatre: " + e.getMessage());
        }
        return "redirect:/admin/theatres";
    }

    @PostMapping("/theatres/{id}/delete")
    public String deleteTheatre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Theatre theatre = theatreRepo.findById(id).orElse(null);
            if (theatre != null) {
                // Check for existing showtimes
                List<Showtime> showtimes = showtimeRepo.findByTheatreOrderByShowtimeAsc(theatre);
                if (!showtimes.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Cannot delete theatre with existing showtimes!");
                    return "redirect:/admin/theatres";
                }
                theatreRepo.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Theatre deleted successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting theatre: " + e.getMessage());
        }
        return "redirect:/admin/theatres";
    }

    // ===================== MANAGER MANAGEMENT =====================
    @GetMapping("/managers")
    public String showManagerManagement(Model model) {
        List<User> managers = userRepo.findAll().stream()
                .filter(user -> "ROLE_MANAGER".equals(user.getRole()))
                .collect(Collectors.toList());

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

    @GetMapping("/register/manager")
    public String showManagerRegistrationForm(Model model) {
        model.addAttribute("managerDto", new ManagerRegistrationDto());
        model.addAttribute("allTheatres", theatreRepo.findAll());
        return "register-manager";
    }

    @PostMapping("/register/manager")
    public String registerManager(@ModelAttribute("managerDto") ManagerRegistrationDto managerDto,
                                  Model model, RedirectAttributes redirectAttributes) {
        if (userService.existsByUsernameOrEmail(managerDto.getUsername(), managerDto.getEmail())) {
            model.addAttribute("error", "Username or email already exists.");
            model.addAttribute("allTheatres", theatreRepo.findAll());
            return "register-manager";
        }

        try {
            // Create user registration DTO
            UserRegistrationDto userDto = new UserRegistrationDto();
            userDto.setUsername(managerDto.getUsername());
            userDto.setEmail(managerDto.getEmail());
            userDto.setPassword(managerDto.getPassword());

            boolean success = userService.registerUser(userDto, "ROLE_MANAGER");
            if (success) {
                // Assign theatres to the newly created manager
                User newManager = userRepo.findByUsername(managerDto.getUsername()).orElse(null);
                if (newManager != null && managerDto.getTheatreIds() != null) {
                    theatreService.assignTheatresToManager(newManager.getId(), managerDto.getTheatreIds());
                }
                redirectAttributes.addFlashAttribute("success", "Manager registered successfully!");
                return "redirect:/admin/managers";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
        }

        model.addAttribute("allTheatres", theatreRepo.findAll());
        return "register-manager";
    }

    @PostMapping("/managers/update")
    public String updateManager(@ModelAttribute ManagerUpdateDto managerDto,
                                RedirectAttributes redirectAttributes) {
        try {
            User manager = userRepo.findById(managerDto.getId()).orElse(null);
            if (manager != null) {
                manager.setUsername(managerDto.getUsername());
                manager.setEmail(managerDto.getEmail());
                userRepo.save(manager);

                theatreService.assignTheatresToManager(manager.getId(), managerDto.getTheatreIds());
                redirectAttributes.addFlashAttribute("success", "Manager updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating manager: " + e.getMessage());
        }
        return "redirect:/admin/managers";
    }

    @PostMapping("/managers/{id}/delete")
    public String deleteManager(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User manager = userRepo.findById(id).orElse(null);
            if (manager != null) {
                // Unassign all theatres from this manager
                List<Theatre> managedTheatres = theatreRepo.findByManager(manager);
                for (Theatre theatre : managedTheatres) {
                    theatre.setManager(null);
                    theatreRepo.save(theatre);
                }
                userRepo.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Manager deleted successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting manager: " + e.getMessage());
        }
        return "redirect:/admin/managers";
    }
}