package com.example.hotelmanagement.controller;

import com.example.hotelmanagement.dto.CustomerDto;
import com.example.hotelmanagement.entity.Customer;
import com.example.hotelmanagement.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SingupController {

    @Autowired private CustomerService customerService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("customer", new CustomerDto());
        return "signup";
    }

    @PostMapping("/signup")
    public String register(@ModelAttribute("customer") CustomerDto dto, Model model) {
        Customer customer = customerService.toEntity(dto);
        customerService.createCustomer(customer);
        model.addAttribute("success", "Account created! Please login.");
        return "login";
    }
}
