package com.example.travelhana.Dto.Payment;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpdatePaymentHistoryDto {

	Long id;
	String memo;
	String Category;

}
