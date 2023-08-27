package com.example.travelhana.Service;

import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Payment.PaymentRequestDto;
import com.example.travelhana.Dto.Payment.PaymentMemoDto;
import org.springframework.http.ResponseEntity;

public interface PaymentService {

	ResponseEntity<?> payment(Users users, PaymentRequestDto paymentListDto);

	ResponseEntity<?> updatePaymentHistory(Users users, Long paymentId, PaymentMemoDto paymentMemoDto);

	ResponseEntity<?> cancelPayment(Users users, Long paymentId);

}