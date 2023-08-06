package com.example.travelhana.Dto;

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

	private String unit;

	private String store;

	private String category;

	private LocalDateTime createdAt;

	private Double lat;

	private Double lng;

	private String address;

	private String memo;

	private int userId;

	private int keyMoneyId;

	private Boolean isSuccess;

	public PaymentHistoryDto(PaymentHistory paymentHistory) {
		this.address = paymentHistory.getAddress();
		this.id = paymentHistory.getId();
		this.balance = paymentHistory.getBalance();
		this.lat = paymentHistory.getLat();
		this.lng = paymentHistory.getLng();
		this.category = paymentHistory.getCategory();
		this.createdAt = paymentHistory.getCreatedAt();
		this.keyMoneyId = paymentHistory.getKeyMoneyId();
		this.isSuccess = paymentHistory.getIsSuccess();
		this.memo = paymentHistory.getMemo();
		this.price = paymentHistory.getPrice();
		this.store = paymentHistory.getStore();
		this.unit = paymentHistory.getUnit();
		this.userId = paymentHistory.getUserId();
	}
}
