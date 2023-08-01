package com.example.travelhana.Dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class PaymentListDto {
    Long price;
    String unit;
    String store;
    String category;
    String address;
    String memo;
    LocalDateTime createdAt;
    Double lat;
    Double lng;

}
