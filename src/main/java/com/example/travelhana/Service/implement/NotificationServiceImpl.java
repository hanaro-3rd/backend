package com.example.travelhana.Service.implement;

import com.example.travelhana.Domain.Notification;
import com.example.travelhana.Dto.Notification.NotificationRequestDto;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Repository.NotificationRepository;
import com.example.travelhana.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;

	@Override
	public ResponseEntity<?> sendNotification(NotificationRequestDto dto) {
		Notification notification = Notification.builder()
				.type(dto.getType())
				.channelId(dto.getChannelId())
				.sender(dto.getSender())
				.data(dto.getData())
				.build();

		notificationRepository.save(notification);
		ApiResponse apiResponse = ApiResponse.builder()
				.resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
				.resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
				.result(notification)
				.build();

		return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
	}

}
