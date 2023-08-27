package com.example.travelhana.Controller;

import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Payment.PaymentRequestDto;
import com.example.travelhana.Dto.Payment.PaymentMemoDto;
import com.example.travelhana.Service.PaymentService;
import com.example.travelhana.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

	private final UserService userService;
	private final PaymentService paymentService;

	@PostMapping("") //결제하기 POST
	public ResponseEntity<?> pay(
			@RequestHeader(value = "Authorization") String accessToken,
			@RequestBody PaymentRequestDto paymentListDto) {
		Users users = userService.getUserByAccessToken(accessToken);
		return paymentService.payment(users, paymentListDto);
	}

	@PatchMapping("/{paymentId}/update") //결제내역 메모 또는 카테고리 수정
	public ResponseEntity<?> updatePayment(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable Long paymentId,
			@RequestBody PaymentMemoDto paymentMemoDto) {
		Users users = userService.getUserByAccessToken(accessToken);
		return paymentService.updatePaymentHistory(users, paymentId, paymentMemoDto);
	}

	@PatchMapping("/{paymentId}/cancel")
	public ResponseEntity<?> deletePayment(
			@RequestHeader(value = "Authorization") String accessToken,
			@PathVariable Long paymentId) {
		Users users = userService.getUserByAccessToken(accessToken);
		return paymentService.cancelPayment(users, paymentId);
	}
}