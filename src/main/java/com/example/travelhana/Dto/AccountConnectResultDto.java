package com.example.travelhana.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AccountConnectResultDto {

	int userId;
	int accountId;
	String accountNum;
	String bank;
	Long balance;

}
