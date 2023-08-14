package com.example.travelhana.Dto.Marker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MarkerDummyDto {

	Double lat;
	Double lng;
	Long amount;
	String place;
	int limitAmount;
	String unit;

}
