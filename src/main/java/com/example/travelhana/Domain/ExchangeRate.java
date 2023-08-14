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
public class ExchangeRate {

	@Id
	@Column(name = "EXCHANGERATE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Double exchangeRate;

	@Column
	private String unit;

	@Column
	private LocalDateTime updatedAt;

	@Column
	private Double changePrice;

}