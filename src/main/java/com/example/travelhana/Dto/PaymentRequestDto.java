package com.example.travelhana.Dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentListDto {
    Long price;
    String unit;
    String store;
    String category;
    String address;
    String memo;
    Double lat;
    Double lng;

}
