package com.example.travelhana.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ExchangeHistory {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name="EXCHANGEHISTORY_ID")
    private Long id;

    @Column
    private Long userId;

    @Column
    private Long keyId;

    @Column
    private Long accountId;

    @Column
    private Long won;

    @Column
    private Long foreignCurrency;

    @Column
    private Boolean isBought;

    @Column
    private Double exchangeRate;

    @Column
    private LocalDateTime exchangeDate;

    @Column
    private Boolean isBusinessday;



}
