package com.example.hotelmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages= {"com.example.hotelmanagement.controller","com.example.hotelmanagement.entity","com.example.hotelmanagement.repo", "com.example.hotelmanagement.configuration", "com.example.hotelmanagement.services","com.example.hotelmanagement.enum","com.example.hotelmanagement.configuration,com.example.hotelmanagement.handler"})
@EntityScan(basePackages = "com.example.hotelmanagement.entity")
public class HotelmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelmanagementApplication.class, args);
	}

}
