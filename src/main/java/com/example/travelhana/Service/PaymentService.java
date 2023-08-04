package com.example.travelhana.Service;

import com.example.travelhana.Dto.PaymentRequestDto;
import com.example.travelhana.Dto.PaymentMemoDto;
import org.springframework.http.ResponseEntity;

public interface PaymentService {

	ResponseEntity<?> payment(String accessToken, PaymentRequestDto paymentListDto);

	ResponseEntity<?> showPaymentHistory(String accessToken, String unit);

	ResponseEntity<?> updatePaymentHistory(String accessToken, PaymentMemoDto paymentMemoDto);

	ResponseEntity<?> deletePaymentHistory(String accessToken, Long payHistoryId);

}
