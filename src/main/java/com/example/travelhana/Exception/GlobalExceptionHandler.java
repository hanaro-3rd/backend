package com.example.travelhana.Exception;

import com.example.travelhana.Dto.MessageDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<MessageDto> handleBusinessException(BusinessException e) {
        log.error("에러가 발생했습니다. : {}", e.getMessage(), e);
        MessageDto messageDto = new MessageDto(e.getErrorCode().getStatusCode(), e.getMessage());
        return new ResponseEntity<>(messageDto, HttpStatus.valueOf(e.getErrorCode().getStatusCode()));
    }

}