package com.example.travelhana.Dto.Plan;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanPaymentHistoryDto {
    private Long id;

    private Long price;

    private Long balance;

    private String store;

    private String category;

    private LocalDateTime createdAt;

    private Double lat;

    private Double lng;

    private String address;

    private String memo;

    private String unit;

    private Boolean isPayment;
}
