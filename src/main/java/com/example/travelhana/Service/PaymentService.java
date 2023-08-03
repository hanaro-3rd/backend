package com.example.travelhana.Service;

import ch.qos.logback.core.status.ErrorStatus;
import com.example.travelhana.Domain.KeyMoney;
import com.example.travelhana.Domain.PaymentHistory;
import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.*;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Handler.BusinessExceptionHandler;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Exception.Response.ErrorResponse;
import com.example.travelhana.Repository.KeyMoneyRepository;
import com.example.travelhana.Repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserServiceImpl userService;
    private final KeyMoneyRepository keyMoneyRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    public ResponseEntity<?> payment(String accessToken, PaymentListDto paymentListDto) {
        try{
            User user =  userService.getUser(accessToken);
            int getUserId = user.getId();
            KeyMoney keymoney = keyMoneyRepository.findByUserIdAndUnit(getUserId, paymentListDto.getUnit());
            if(keymoney==null){
                throw new BusinessExceptionHandler(ErrorCode.NO_KEYMONEY);
            }
            Long nowBalance = keymoney.getBalance() - paymentListDto.getPrice();
            if(nowBalance < 0) { //잔액부족 에러처리
                throw new BusinessExceptionHandler(ErrorCode.INSUFFICIENT_BALANCE);
            }

            keymoney.updateMinusBalance(paymentListDto.getPrice());
            keyMoneyRepository.save(keymoney);
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

            PaymentHistory responsePaymentHistory = paymentHistoryRepository.save(paymentHistory);
            PaymentHistoryDto paymentHistoryDto = PaymentHistoryDto.builder()
                    .price(responsePaymentHistory.getPrice())
                    .balance(responsePaymentHistory.getBalance())
                    .unit(responsePaymentHistory.getUnit())
                    .store(responsePaymentHistory.getStore())
                    .category(responsePaymentHistory.getCategory())
                    .createdAt(responsePaymentHistory.getCreatedAt())
                    .lat(responsePaymentHistory.getLat())
                    .lng(responsePaymentHistory.getLng())
                    .address(responsePaymentHistory.getAddress())
                    .memo(responsePaymentHistory.getMemo())
                    .userId(responsePaymentHistory.getUserId())
                    .keyMoneyId(responsePaymentHistory.getKeyMoneyId())
                    .isSuccess(responsePaymentHistory.getIsSuccess())
                    .id(responsePaymentHistory.getId())
                    .build();
            ApiResponse apiResponse = ApiResponse.builder()
                    .result(paymentHistoryDto)
                    .resultCode(SuccessCode.INSERT_SUCCESS.getStatusCode())
                    .resultMsg(SuccessCode.INSERT_SUCCESS.getMessage())
                    .build();

            return new ResponseEntity<>(apiResponse,HttpStatus.CREATED);
        } catch (BusinessExceptionHandler e){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorMessage(e.getMessage())
                    .errorCode(e.getErrorCode().getStatusCode())
                    .build();
            return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    public ResponseEntity<?> showPaymentHistory (String accessToken) {
        try{
            User user =  userService.getUser(accessToken);
            int getUserId = user.getId();
            KeyMoney keyMoney = keyMoneyRepository.findByUser_Id(getUserId);
            List<PaymentHistory> paymentHistories =  paymentHistoryRepository.findAllByKeyMoneyIdAndIsSuccess(keyMoney.getId(), true);
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
            ApiResponse apiResponse = ApiResponse.builder()
                    .result(paymentListDtos)
                    .resultCode(SuccessCode.SELECT_SUCCESS.getStatusCode())
                    .resultMsg(SuccessCode.SELECT_SUCCESS.getMessage())
                    .build();
            return new ResponseEntity<>(apiResponse,HttpStatus.OK);
        }
        catch(BusinessExceptionHandler e){
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorMessage(e.getMessage())
                    .errorCode(e.getErrorCode().getStatusCode())
                    .build();
            return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    @Transactional //객체
    public ResponseEntity<?> updatePaymentHistory(String accessToken, PaymentMemoDto paymentMemoDto) {
        try{
            User user =  userService.getUser(accessToken);
            int getUserId = user.getId();
            PaymentHistory paymentHistory = paymentHistoryRepository.findByIdAndUserId(paymentMemoDto.getId(),getUserId);
            if(paymentHistory==null) {
                throw new BusinessExceptionHandler(ErrorCode.INVALID_UPDATE);
            }
            PaymentHistory updatePaymentHistory= PaymentHistory
                    .builder()
                    .price(paymentHistory.getPrice())
                    .balance(paymentHistory.getBalance())
                    .unit(paymentHistory.getUnit())
                    .store(paymentHistory.getStore())
                    .category(paymentMemoDto.getCategory())
                    .createdAt(paymentHistory.getCreatedAt())
                    .lat(paymentHistory.getLat())
                    .lng(paymentHistory.getLng())
                    .address(paymentHistory.getAddress())
                    .memo(paymentMemoDto.getMemo())
                    .userId(paymentHistory.getUserId())
                    .keyMoneyId(paymentHistory.getKeyMoneyId())
                    .isSuccess(paymentHistory.getIsSuccess())
                    .id(paymentHistory.getId())
                    .build();
            PaymentHistory responsePaymentHistory = paymentHistoryRepository.save(updatePaymentHistory);
            UpdatePaymentHistoryDto updatePaymentHistoryDto = UpdatePaymentHistoryDto.builder()
                    .id(responsePaymentHistory.getId())
                    .Category(responsePaymentHistory.getCategory())
                    .memo(responsePaymentHistory.getMemo())
                    .build();
            ApiResponse apiResponse = ApiResponse.builder()
                    .result(updatePaymentHistoryDto)
                    .resultCode(SuccessCode.UPDATE_SUCCESS.getStatusCode())
                    .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
                    .build();
            return new ResponseEntity<>(apiResponse,HttpStatus.ACCEPTED);
        }catch (RuntimeException e) { //
            return new ResponseEntity<>("서버내부 오류",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
// 취소되면 승인여부 false
    // 키머니 잔액 update
     public ResponseEntity<?> deletePaymentHistory (String accessToken, Long payHistoryId) {
        try {
            User user = userService.getUser(accessToken);
            int getUserId = user.getId();
            PaymentHistory paymentHistory = paymentHistoryRepository.findByIdAndUserId(payHistoryId,getUserId);
            if(paymentHistory==null) {
                throw new BusinessExceptionHandler(ErrorCode.INVALID_UPDATE);
            }
            // isSuccess false로 리턴
            PaymentHistory updatePaymentHistory= PaymentHistory
                    .builder()
                    .price(paymentHistory.getPrice())
                    .balance(paymentHistory.getBalance())
                    .unit(paymentHistory.getUnit())
                    .store(paymentHistory.getStore())
                    .category(paymentHistory.getCategory())
                    .createdAt(paymentHistory.getCreatedAt())
                    .lat(paymentHistory.getLat())
                    .lng(paymentHistory.getLng())
                    .address(paymentHistory.getAddress())
                    .memo(paymentHistory.getMemo())
                    .userId(paymentHistory.getUserId())
                    .keyMoneyId(paymentHistory.getKeyMoneyId())
                    .isSuccess(false)
                    .id(paymentHistory.getId())
                    .build();
            KeyMoney keymoney = keyMoneyRepository.findByUserIdAndUnit(getUserId, updatePaymentHistory.getUnit());
            if (keymoney == null) {
                throw new BusinessExceptionHandler(ErrorCode.NO_KEYMONEY);
            }
            keymoney.updatePlusBalance(updatePaymentHistory.getPrice());
            KeyMoney responseKeymoney = keyMoneyRepository.save(keymoney);
            PaymentHistory responsePaymentHistory = paymentHistoryRepository.save(updatePaymentHistory);

            KeymoneyDto keymoneyDto = KeymoneyDto.builder()
                    .balance(responseKeymoney.getBalance())
                    .id(responseKeymoney.getId())
                    .user(responseKeymoney.getUser())
                    .unit(responseKeymoney.getUnit())
                    .build();
            PaymentHistoryDto paymentHistoryDto = PaymentHistoryDto.builder()
                    .price(responsePaymentHistory.getPrice())
                    .balance(responsePaymentHistory.getBalance())
                    .unit(responsePaymentHistory.getUnit())
                    .store(responsePaymentHistory.getStore())
                    .category(responsePaymentHistory.getCategory())
                    .createdAt(responsePaymentHistory.getCreatedAt())
                    .lat(responsePaymentHistory.getLat())
                    .lng(responsePaymentHistory.getLng())
                    .address(responsePaymentHistory.getAddress())
                    .memo(responsePaymentHistory.getMemo())
                    .userId(responsePaymentHistory.getUserId())
                    .keyMoneyId(responsePaymentHistory.getKeyMoneyId())
                    .isSuccess(responsePaymentHistory.getIsSuccess())
                    .id(responsePaymentHistory.getId())
                    .build();
            Map<String,Object> responseData = new HashMap<>();
            responseData.put("totalbalance",keymoneyDto.getBalance());
            responseData.put("paymentHistory",paymentHistoryDto);
            ApiResponse apiResponse = ApiResponse.builder()
                    .result(responseData)
                    .resultCode(SuccessCode.UPDATE_SUCCESS.getStatusCode())
                    .resultMsg(SuccessCode.UPDATE_SUCCESS.getMessage())
                    .build();
         return new ResponseEntity<>(apiResponse,HttpStatus.ACCEPTED);
        } catch(BusinessExceptionHandler e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorMessage(e.getMessage())
                    .errorCode(e.getErrorCode().getStatusCode())
                    .build();
            return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
