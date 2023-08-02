package com.example.travelhana.Object;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExchangeSuccess {
    private Long won;
    private Long key;
    private Long keymoneyBalance; //키머니 잔액
    private Boolean isBought; //원화, 외화 환전방향
    private Long balance;

}
