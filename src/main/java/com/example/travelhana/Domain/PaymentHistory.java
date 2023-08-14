package com.example.travelhana.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class PaymentHistory {

	@Id
	@Column(name = "PAYMENTHISTORY_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Long price;

	@Column
	private Long balance;

	@Column
	private String store;

	@Column
	private String category;

	@Column
	private LocalDateTime createdAt;

	@Column
	private Double lat;

	@Column
	private Double lng;

	@Column
	private String address;

	@Column
	private String memo;

	@Column
	private int userId;

	@Column
	private int keymoneyId;

	@Column
	private Boolean isPayment;

	public void updateCategoryAndMemo(String category, String memo) {
		this.category = category;
		this.memo = memo;
	}

}