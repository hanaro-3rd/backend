package com.example.travelhana.Dto.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AccountInformation {

	int userId;
	int accountId;
	String accountNum;
	String bank;
	Long balance;

}
