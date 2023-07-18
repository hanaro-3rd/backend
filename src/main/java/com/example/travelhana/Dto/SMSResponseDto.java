package com.example.travelhana.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SMSResponseDto {

    private String requestId;
    private LocalDateTime requestTime;
    private String statusCode;
    private String statusName;

}
