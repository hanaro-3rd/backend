package com.example.travelhana.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Marker {

	@Id
	@Column(name="MARKER_ID")
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

	public void decreaseLimitAmount() {
		this.limitAmount -= 1;
	}

}