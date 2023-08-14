package com.example.travelhana.Dto.Payment;

import com.example.travelhana.Domain.PaymentHistory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentHistoryDto {

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

	private int userId;

	private int keymoneyId;

	private Boolean isPayment;

}
