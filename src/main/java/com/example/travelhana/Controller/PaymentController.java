package com.example.travelhana.Controller;

import com.example.travelhana.Dto.PaymentDto;
import com.example.travelhana.Dto.RequestPaymentDto;
import com.example.travelhana.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    @PostMapping("/pay")
    public ResponseEntity<?> pay (@RequestHeader(value = "Authorization") String accessToken,
                               @RequestBody RequestPaymentDto requestPaymentDto) {

        return paymentService.payment(accessToken, requestPaymentDto);
    }

    @GetMapping("/payhistory")
    public void getPayhistory() {}

    @PatchMapping("/payhistory")
    public void updatePayhistory() {}

}
