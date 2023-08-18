package com.example.travelhana.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
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
	private int keymoneyId;

	@Column
	private int accountId;

	@Column
	private Long exchangeWon;

	@Column
	private Long exchangeKey;

	@Column
	private Boolean isBought;

	@Column
	private Double exchangeRate;

	@Column
	@CreationTimestamp
	private final LocalDateTime exchangeDate=LocalDateTime.now();

	@Column
	private Boolean isBusinessday;

	@Column
	private Long balance;

}