package com.example.travelhana.Controller;

import com.example.travelhana.Dto.PaymentDto;
import com.example.travelhana.Dto.PaymentListDto;
import com.example.travelhana.Dto.PaymentMemoDto;
import com.example.travelhana.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    @PostMapping("/pay")
    public ResponseEntity<?> pay (@RequestHeader(value = "Authorization") String accessToken, @RequestBody PaymentDto paymentDto) {
        return paymentService.payment(accessToken, paymentDto);
    }


    @GetMapping("/payhistory")
    public ResponseEntity<?> getPayhistory(@RequestHeader(value = "Authorization") String accessToken) {
         return paymentService.showPaymentHistory(accessToken);
    }

    @PatchMapping("/payhistory")
    public ResponseEntity<?> updatePayhistory(@RequestHeader(value = "Authorization") String accessToken, @RequestBody PaymentMemoDto paymentMemoDto) {
        return paymentService.updatePaymentHistory(accessToken, paymentMemoDto);
    }

}
