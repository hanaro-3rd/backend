package com.example.travelhana.Service;

import com.example.travelhana.Dto.Notification.NotificationRequestDto;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
	ResponseEntity<?> sendNotification(NotificationRequestDto dto);
}
