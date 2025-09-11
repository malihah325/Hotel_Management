package com.example.hotelmanagement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.filter.HiddenHttpMethodFilter;



@SpringBootApplication
@ComponentScan(basePackages= {"com.example.hotelmanagement.controller","com.example.hotelmanagement.entity","com.example.hotelmanagement.repo", "com.example.hotelmanagement.configuration", "com.example.hotelmanagement.services","com.example.hotelmanagement.enum","com.example.hotelmanagement.configuration,com.example.hotelmanagement.handler","com.example.hotelmanagement.helperClass","package com.example.hotelmanagement.converters"})
@EntityScan(basePackages = "com.example.hotelmanagement.entity")
@EnableScheduling
public class HotelmanagementApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(HotelmanagementApplication.class, args);
		
	}
	 // Enable support for PUT/DELETE in forms
    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

}
