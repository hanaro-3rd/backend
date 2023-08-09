package com.example.travelhana.Service;

import com.example.travelhana.Dto.PaymentRequestDto;
import com.example.travelhana.Dto.PaymentMemoDto;
import org.springframework.http.ResponseEntity;

public interface PaymentService {

	ResponseEntity<?> payment(String accessToken, PaymentRequestDto paymentListDto);

	ResponseEntity<?> updatePaymentHistory(String accessToken, Long paymentId, PaymentMemoDto paymentMemoDto);

	ResponseEntity<?> deletePaymentHistory(String accessToken, Long paymentId);

}
