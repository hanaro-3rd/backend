package com.example.travelhana.Dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class PaymentDto {
    int keyId;
    Boolean isSuccessed;
    int userId;
    RequestPaymentDto requestPaymentDto;
}
