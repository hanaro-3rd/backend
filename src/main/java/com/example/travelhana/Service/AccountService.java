package com.example.travelhana.Service;

import com.example.travelhana.Domain.Users;
import com.example.travelhana.Dto.Account.*;

import org.springframework.http.ResponseEntity;

public interface AccountService {

	ResponseEntity<?> getConnectedAccountList(Users users) throws Exception;

	ResponseEntity<?> findExternalAccountList(Users users) throws Exception;

	ResponseEntity<?> connectExternalAccount(
			Users users, int externalAccountId, AccountPasswordDto accountPasswordDto) throws Exception;

}