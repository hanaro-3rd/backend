package com.example.travelhana.Service;

import org.springframework.http.ResponseEntity;

public interface KeymoneyService {

	ResponseEntity<?> getKeymoney(String accessToken);

	ResponseEntity<?> getKeymoneyHistory(String accessToken, String unit, String filter);

	ResponseEntity<?> getDetailKeymoneyHistory(String accessToken, Long historyId, String type);

}
