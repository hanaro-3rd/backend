package com.example.travelhana.Service;

import com.example.travelhana.Dto.CodeRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.springframework.http.ResponseEntity;

public interface PhoneAuthService {
    ResponseEntity<?> checkCode(CodeRequestDto codeDto);
    ResponseEntity<?> sendMessageWithResttemplate(String phoneNum)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, JsonProcessingException, URISyntaxException;

}
