package com.example.travelhana.Object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeSuccess {

    private Long won;
    private Long key;
    private Long keymoneyBalance;
    private Boolean isBought;

}
