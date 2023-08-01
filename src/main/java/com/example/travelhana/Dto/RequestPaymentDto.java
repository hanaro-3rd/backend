package com.example.travelhana.Dto;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RequestPaymentDto {
    Long price;
    String unit;
    String store;
    String category;
    String address;
    LocalDateTime createdAt;
    Double lat;
    Double lng;
}
