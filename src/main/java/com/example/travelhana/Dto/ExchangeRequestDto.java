package com.example.travelhana.Dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeRequestDto {
    private int accountId;
    private Long money; //환전 요청할 원화 or 외화
    private Double exchangeRate;
    private Boolean isBusinessday;
    private Boolean isBought;
    private Boolean isNow;
    private String unit;


}
