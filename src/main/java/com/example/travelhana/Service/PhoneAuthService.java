package com.example.travelhana.Service;


import com.example.travelhana.Dto.*;
import com.example.travelhana.Exception.Code.ErrorCode;
import com.example.travelhana.Exception.Code.SuccessCode;
import com.example.travelhana.Exception.Response.ApiResponse;
import com.example.travelhana.Exception.Response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PhoneAuthService {
    @Value("${SMS_ACCESS_KEY}")
    private String accesskey;
    @Value("${SMS_SECRETE_KEY}")
    private String secretkey;
    @Value("${SMS_SERVICE_ID}")
    private String serviceid;
    @Value("${SMS_FROM_NUMBER}")
    private String fromNum;

    private final HttpSession session;

    public static int generateRandomNumber() {
        return ThreadLocalRandom.current().nextInt(100000, 1000000);
    }

    private String makeSignature(Long time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/" + serviceid + "/messages";
        String timestamp = time.toString();
        String accessKey = accesskey;
        String secretKey = secretkey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }

    public ResponseEntity<?> sendMessageWithResttemplate(String phoneNum) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException, URISyntaxException, UnsupportedEncodingException {
        Long time = System.currentTimeMillis();
        String code = String.valueOf(generateRandomNumber());

        setCodeIntoSession(code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", accesskey);
        headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

        MessageDto msg = new MessageDto(phoneNum, "휴대폰 인증 코드는 "+code+"입니다.");
        List<MessageDto> messages = new ArrayList<>();
        messages.add(msg);
        SMSRequestDto request = SMSRequestDto.builder()
                .type("SMS")
                .from(fromNum)
                .content(msg.getContent())
                .messages(messages)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(request);
        HttpEntity<String> httpBody = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        SMSResponseDto response = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/" + serviceid + "/messages"), httpBody, SMSResponseDto.class);
        ApiResponse apiResponse= ApiResponse.builder()
                .result(response)
                .resultCode(SuccessCode.OPEN_API_SUCCESS.getStatusCode())
                .resultMsg(SuccessCode.OPEN_API_SUCCESS.getMessage())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
    }

    private void setCodeIntoSession(String code)
    {
        session.setAttribute("code", code);
        session.setMaxInactiveInterval(180); //코드 유효기간 3뷴
    }

    public ResponseEntity<?> checkCode(String inputCode)
    {
        //3분이 지나면 세션이 만료되는거랑, 코드가 틀린거랑 어떻게 구별할건지?
        //세션 아이디는 존재하지만 일치하지 않는거
        //세션 자체가 존재하지 않는거

        String code = (String) session.getAttribute("code");
        if(code!=null)
        {
            if (inputCode.equals(code)) {
                session.removeAttribute("code");
                ApiResponse apiResponse=ApiResponse.builder()
                        .resultCode(SuccessCode.AUTH_SUCCESS.getStatusCode())
                        .resultMsg(SuccessCode.AUTH_SUCCESS.getMessage())
                        .build();
                return ResponseEntity.ok(apiResponse);

            }
            else{
                ErrorResponse errorResponse=ErrorResponse.builder()
                        .errorCode(ErrorCode.AUTH_FAILURE.getStatusCode())
                        .errorMessage(ErrorCode.AUTH_FAILURE.getMessage())
                        .build();
                return ResponseEntity.ok(errorResponse);
            }
        }

        else {
            session.removeAttribute("code");
            ErrorResponse errorResponse=ErrorResponse.builder()
                    .errorMessage(ErrorCode.SESSION_INVALID.getMessage())
                    .errorCode(ErrorCode.SESSION_INVALID.getStatusCode())
                    .build();
            return ResponseEntity.ok(errorResponse);
        }
    }

}