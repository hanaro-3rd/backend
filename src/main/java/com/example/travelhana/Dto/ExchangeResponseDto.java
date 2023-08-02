package com.example.travelhana.Dto;

import lombok.*;
import org.hibernate.procedure.spi.ParameterRegistrationImplementor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ExchangeResponseDto {

    private Long won;
    private Long key;
    private Double exchangeRage;
}
