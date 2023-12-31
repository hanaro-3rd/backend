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
public class Marker {

	@Id
	@Column(name = "MARKER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column
	private Double lat;

	@Column
	private Double lng;

	@Column
	private Long amount;

	@Column
	private String place;

	@Column
	private int limitAmount;

	@Column
	private String unit;

	@Column
	private String address;

	public void decreaseLimitAmount() {
		this.limitAmount -= 1;
	}

}