package com.example.travelhana.Dto.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class AccountListDto {

	List<AccountConnectResultDto> externalAccounts;

}
