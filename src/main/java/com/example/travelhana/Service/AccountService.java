package com.example.travelhana.Service;

import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Account.*;

import org.springframework.http.ResponseEntity;

public interface AccountService {

	ResponseEntity<?> getConnectedAccountList(Users user) throws Exception;

	ResponseEntity<?> findExternalAccountList(Users user) throws Exception;

	ResponseEntity<?> connectExternalAccount(
			String accessToken, int externalAccountId, AccountPasswordDto accountPasswordDto) throws Exception;

}