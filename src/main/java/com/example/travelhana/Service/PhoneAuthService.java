package com.example.travelhana.Service;


import com.example.travelhana.Dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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

    public static int generateRandomNumber() {
        return ThreadLocalRandom.current().nextInt(100000, 1000000);
    }

    public String makeSignature(Long time) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
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

    public SMSAndCodeDto sendMessageWithResttemplate(String phoneNum) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException, URISyntaxException, UnsupportedEncodingException {
        Long time = System.currentTimeMillis();
        String code = String.valueOf(generateRandomNumber());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", accesskey);
        headers.set("x-ncp-apigw-signature-v2", makeSignature(time));

        MessageDto msg = new MessageDto(phoneNum, code);
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

        return new SMSAndCodeDto(response, code);
    }

}
