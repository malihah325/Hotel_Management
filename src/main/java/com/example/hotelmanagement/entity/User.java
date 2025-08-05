package com.example.hotelmanagement.entity;

import com.example.hotelmanagement.enums.Role;

public interface User {

	    Long getId();
	    String getUsername();
	    String getPassword();
	    String getEmail();
	    Role getRole();
	    
	}
