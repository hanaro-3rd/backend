package com.example.travelhana.Controller;

import com.example.travelhana.Dto.PaymentDto;
import com.example.travelhana.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    @PostMapping("/pay")
    public void pay (@RequestHeader(value = "Authorization") String accessToken,
                     @RequestBody PaymentDto paymentDto) {

        paymentService.payment(accessToken,paymentDto);
    }

    @GetMapping("/payhistory")
    public void getPayhistory() {}

    @PatchMapping("/payhistory")
    public void updatePayhistory() {}


}
