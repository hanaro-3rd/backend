package com.example.travelhana.Service;

import com.example.travelhana.Domain.KeyMoney;
import com.example.travelhana.Domain.PaymentHistory;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.PaymentDto;
import com.example.travelhana.Dto.RequestPaymentDto;
import com.example.travelhana.Exception.BusinessException;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ErrorResponse;
import com.example.travelhana.Repository.KeyMoneyRepository;
import com.example.travelhana.Repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserServiceImpl userService;
    private final KeyMoneyRepository keyMoneyRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    public ResponseEntity<?> payment(String accesssToken, RequestPaymentDto requestPaymentDto) {

            User user =  userService.getUser(accesssToken);
            int getUserId = user.getId();
            KeyMoney keymoney = keyMoneyRepository.findByUserIdAndUnit(getUserId,requestPaymentDto.getUnit());
            Long nowBalance = keymoney.getBalance() - requestPaymentDto.getPrice();
            if(nowBalance < 0) { //잔액부족 에러처리
                throw new BusinessExceptionHandler(ErrorCode.INSUFFICIENT_BALANCE);
            }
            PaymentHistory paymentHistory =  PaymentHistory.builder()
                    .price(requestPaymentDto.getPrice())
                    .unit(requestPaymentDto.getUnit())
                    .store(requestPaymentDto.getStore())
                    .category(requestPaymentDto.getCategory())
                    .address(requestPaymentDto.getAddress())
                    .createdAt(requestPaymentDto.getCreatedAt())
                    .lat(requestPaymentDto.getLat())
                    .lng(requestPaymentDto.getLng())
                    .balance(nowBalance)
                    .memo("")
                    .userId(getUserId)
                    .keyId(keymoney.getId())
                    .isSuccess(true)
                    .build();

            paymentHistoryRepository.save(paymentHistory);
            return new ResponseEntity<>(paymentHistory, HttpStatus.OK);
    }
}
