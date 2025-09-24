package com.example.hotelmanagement.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String homePage(@RequestParam(value = "error", required = false) String error, 
                           Model model) {
        if (error != null) {
            model.addAttribute("loginError", "❌ Invalid email or password.");
        }
        return "home";
    }
}

