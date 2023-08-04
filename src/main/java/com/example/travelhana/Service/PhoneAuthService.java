package com.example.travelhana.Service;

import com.example.travelhana.Dto.CodeRequestDto;
import org.springframework.http.ResponseEntity;

public interface PhoneAuthService {
    ResponseEntity<?> checkCode(CodeRequestDto codeDto);
    ResponseEntity<?> sendMessageWithResttemplate(String phoneNum);

}
