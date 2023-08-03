package com.example.travelhana.Controller;

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
    @PostMapping("/pay") //결제하기 POST
    public ResponseEntity<?> pay (@RequestHeader(value = "Authorization") String accessToken, @RequestBody PaymentListDto paymentListDto) {
        return paymentService.payment(accessToken, paymentListDto);
    }


    @GetMapping("/payhistory") //결제내역 읽어오기
    public ResponseEntity<?> getPayhistory(@RequestHeader(value = "Authorization") String accessToken) {
         return paymentService.showPaymentHistory(accessToken);
    }

    @PatchMapping("/payhistory") //결제내역 메모 또는 카테고리 수정
    public ResponseEntity<?> updatePayhistory(@RequestHeader(value = "Authorization") String accessToken, @RequestBody PaymentMemoDto paymentMemoDto) {
        return paymentService.updatePaymentHistory(accessToken, paymentMemoDto);
    }

    @PatchMapping("/payhistory/{payhistoryId}")
    public ResponseEntity<?>  deletePayhistory(@PathVariable Long payhistoryId, @RequestHeader(value = "Authorization") String accessToken) {
       return  paymentService.deletePaymentHistory(accessToken,payhistoryId);
    }
}
