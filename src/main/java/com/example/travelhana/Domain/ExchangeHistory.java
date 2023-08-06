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

	@Id
	@Column(name = "EXCHANGEHISTORY_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private int userId;

	@Column
	private int keyId;

	@Column
	private int accountId;

	@Column
	private Long money;

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

	@Column
	private Long keymoneyBalance;

}