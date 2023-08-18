package com.example.travelhana.Service;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetails extends UserDetails {

	String getSalt();
	String getPattern();

}
