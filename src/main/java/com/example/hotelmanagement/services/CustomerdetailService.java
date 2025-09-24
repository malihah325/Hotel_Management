package com.example.hotelmanagement.services;

import com.example.hotelmanagement.entity.Customer;
import com.example.hotelmanagement.repositories.CustomerRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomerdetailService implements UserDetailsService {

    private final CustomerRepo customerRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                customer.getEmail(),
                customer.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + customer.getRole().name()))
        );
    }
}
