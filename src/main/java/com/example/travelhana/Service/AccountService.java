package com.example.travelhana.Service;

import com.example.travelhana.Domain.User;
import com.example.travelhana.Dto.Account.*;

import org.springframework.http.ResponseEntity;

public interface AccountService {

	ResponseEntity<?> getConnectedAccountList(String accessToken) throws Exception;

	void createDummyExternalAccounts(AccountDummyDto accountDummyDto, User user) throws Exception;

	ResponseEntity<?> findExternalAccountList(String accessToken) throws Exception;

	ResponseEntity<?> connectExternalAccount(
			String accessToken, int externalAccountId, AccountPasswordDto accountPasswordDto) throws Exception;

}