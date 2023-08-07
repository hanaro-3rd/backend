package com.example.travelhana.Service;

import org.springframework.http.ResponseEntity;

public interface KeymoneyService {

	ResponseEntity<?> getKeymoney(String accessToken);

}
