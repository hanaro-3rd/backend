package com.example.travelhana.Dto.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AccountDummyDto {

	int userId;
	String accountPassword;
	String registrationNum;
	String phoneNum;

}
