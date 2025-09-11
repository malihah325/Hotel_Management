package com.example.hotelmanagement.controller;
import com.example.hotelmanagement.dto.CustomerSignupDTO;
import com.example.hotelmanagement.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SingupController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/login")
    public String loginPage() {
        return "customer-dashboard";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("customer", new CustomerSignupDTO());
        return "signup";
    }

    @PostMapping("/signup")
    public String register(@ModelAttribute("customer") CustomerSignupDTO dto, Model model) {
        customerService.registerNewCustomer(dto);
        model.addAttribute("success", "Account created successfully! ");
        return "customer-dashboard";
    }

}
