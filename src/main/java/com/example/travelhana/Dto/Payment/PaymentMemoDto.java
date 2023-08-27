package com.example.travelhana.Dto.Payment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentMemoDto {

	String category;
	String memo;

}
