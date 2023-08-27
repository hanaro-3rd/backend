package com.example.travelhana.Service;

import com.example.travelhana.Domain.Users;
import org.springframework.http.ResponseEntity;

public interface KeymoneyService {

	ResponseEntity<?> getKeymoney(Users users);

	ResponseEntity<?> getKeymoneyHistory(Users users, String unit, String filter);

	ResponseEntity<?> getDetailKeymoneyHistory(Users users, Long historyId, String type);

}
