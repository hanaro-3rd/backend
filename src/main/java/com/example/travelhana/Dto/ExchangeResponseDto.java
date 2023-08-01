package com.example.travelhana.Dto;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeResponseDto {
    Long id;
    int userId;
    int keyId;
    int accountId;
    Long won;
    Long foreignCurrency;
    Boolean isBought;
    Double exchangeRate;
    LocalDateTime exchangeDate;
    Boolean isBusinessday;

}
