package com.example.hotelmanagement.services;

import com.example.hotelmanagement.entity.Customer;
import com.example.hotelmanagement.repositories.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomerdetailService implements UserDetailsService {

    @Autowired
    private CustomerRepo customerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	Customer customer = customerRepository.findByEmail(email);
    	if (customer == null) {
    	    throw new UsernameNotFoundException("Customer not found");
    	}


        return new org.springframework.security.core.userdetails.User(
            customer.getEmail(),
            customer.getPassword(),
            Collections.singleton(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
        );
    }
}
