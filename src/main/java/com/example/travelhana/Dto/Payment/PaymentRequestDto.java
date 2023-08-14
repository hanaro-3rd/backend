package com.example.travelhana.Dto.Payment;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDto {

	Long price;
	String unit;
	String store;
	String category;
	String address;
	String memo;
	Double lat;
	Double lng;

}
