package com.example.travelhana.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ExchangeReservation {

	@Id
	@Column(name = "EXCHANGERESERVATION_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private int accountId;

	@Column
	private Long money;

	@Column
	private Boolean isBought;

	@Column
	private Boolean isNow;

	@Column
	private String unit;

}
