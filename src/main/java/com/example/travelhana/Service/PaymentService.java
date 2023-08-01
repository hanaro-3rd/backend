package com.example.travelhana.Service;

import com.example.travelhana.Domain.KeyMoney;
import com.example.travelhana.Domain.PaymentHistory;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.PaymentDto;
import com.example.travelhana.Dto.PaymentListDto;
import com.example.travelhana.Dto.PaymentMemoDto;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Repository.KeyMoneyRepository;
import com.example.travelhana.Repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserServiceImpl userService;
    private final KeyMoneyRepository keyMoneyRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    @Transactional
    public ResponseEntity<?> payment(String accessToken, PaymentDto paymentListDto) {
            User user =  userService.getUser(accessToken);
            int getUserId = user.getId();
            KeyMoney keymoney = keyMoneyRepository.findByUserIdAndUnit(getUserId, paymentListDto.getUnit());
            if(keymoney.getBalance()==null){
                throw new BusinessExceptionHandler(ErrorCode.NO_ACCOUNT);
            }
            Long nowBalance = keymoney.getBalance() - paymentListDto.getPrice();
            if(nowBalance < 0) { //잔액부족 에러처리
                throw new BusinessExceptionHandler(ErrorCode.INSUFFICIENT_BALANCE);
            }

            PaymentHistory paymentHistory =  PaymentHistory.builder()
                    .price(paymentListDto.getPrice())
                    .unit(paymentListDto.getUnit())
                    .store(paymentListDto.getStore())
                    .category(paymentListDto.getCategory())
                    .address(paymentListDto.getAddress())
                    .createdAt(paymentListDto.getCreatedAt())
                    .memo(paymentListDto.getMemo())
                    .lat(paymentListDto.getLat())
                    .lng(paymentListDto.getLng())
                    .balance(nowBalance)
                    .userId(getUserId)
                    .keyMoneyId(keymoney.getId())
                    .isSuccess(true)
                    .build();
            paymentHistoryRepository.save(paymentHistory);
            return new ResponseEntity<>(paymentHistory, HttpStatus.OK);
    }
    public ResponseEntity<?> showPaymentHistory (String accessToken) {
        User user =  userService.getUser(accessToken);
        int getUserId = user.getId();
        KeyMoney keyMoney = keyMoneyRepository.findByUserId(getUserId);
        List<PaymentHistory> paymentHistories =  paymentHistoryRepository.findAllByKeyMoneyId(keyMoney.getId());
        List<PaymentListDto> paymentListDtos = new ArrayList<>();
        for(PaymentHistory paymentHistory : paymentHistories) {
            PaymentListDto paymentListDto = PaymentListDto.builder()
                    .price(paymentHistory.getPrice())
                    .unit(paymentHistory.getUnit())
                    .store(paymentHistory.getStore())
                    .address(paymentHistory.getAddress())
                    .category(paymentHistory.getCategory())
                    .createdAt(paymentHistory.getCreatedAt())
                    .lat(paymentHistory.getLat())
                    .lng(paymentHistory.getLng())
                    .memo(paymentHistory.getMemo())
                    .build();
            paymentListDtos.add(paymentListDto);
        }
        return new ResponseEntity<>(paymentListDtos,HttpStatus.OK);

    }

    @Transactional
    public ResponseEntity<?> updatePaymentHistory(String accessToken, PaymentMemoDto paymentMemoDto) {
        User user =  userService.getUser(accessToken);
        int getUserId = user.getId();
        PaymentHistory paymentHistory = paymentHistoryRepository.findByIdAndUserId(paymentMemoDto.getId(),getUserId);
        if(paymentHistory==null) {
            throw new BusinessExceptionHandler(ErrorCode.INVALID_UPDATE);
        }
        paymentHistory.setMemo(paymentMemoDto.getMemo());
        paymentHistory.setCategory(paymentMemoDto.getCategory());
        paymentHistoryRepository.save(paymentHistory);
        return new ResponseEntity<>(paymentHistory,HttpStatus.OK);
    }
}
