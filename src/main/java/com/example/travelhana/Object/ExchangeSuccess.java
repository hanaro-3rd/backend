package com.example.travelhana.Object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeSuccess {

    private Long won;
    private Long key;
    private Long keymoneyBalance;
    private Boolean isBought;

}
