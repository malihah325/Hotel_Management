package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.CustomerSignupDTO;
import com.example.hotelmanagement.services.CustomerService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SignupController {

    private final CustomerService customerService;

    // ---------------- LOGIN ----------------
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg", "❌ Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "✅ You have been logged out successfully.");
        }
        return "home"; // we show login modal inside home.html
    }

    // ---------------- SIGNUP ----------------
    @GetMapping("/signup")
    public String signupPage() {
        return "home"; // signup modal is also in home.html
    }

    @PostMapping("/signup")
    public String registerCustomer(@ModelAttribute CustomerSignupDTO signupDTO) {
        customerService.registerNewCustomer(signupDTO);
        // after successful signup -> redirect to home with param
        return "redirect:/?signupSuccess";
    }
}
