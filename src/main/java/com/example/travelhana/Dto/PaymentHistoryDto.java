package com.example.travelhana.Dto;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Builder @Getter
public class PaymentHistoryDto {
     private Long id;

     private Long price;

     private Long balance;

     private String unit;

     private String store;

     private String category;

     private LocalDateTime createdAt;

     private Double lat;

     private Double lng;

     private String address;

     private String memo;

     private Integer userId;

     private Integer keyMoneyId;

     private Boolean isSuccess;
}
