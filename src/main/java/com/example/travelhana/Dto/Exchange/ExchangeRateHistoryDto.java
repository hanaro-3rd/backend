package com.example.travelhana.Dto.Exchange;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ExchangeRateHistoryDto {

    private Double exchangeRate;
    private Double changePrice;
    private String unit;
    private LocalDateTime updatedAt;

}
