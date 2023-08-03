package com.example.travelhana.Service;

import com.example.travelhana.Dto.Account.*;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

public interface AccountService {

	ResponseEntity<ConnectedAccountListDto> getConnectedAccountList(String accessToken) throws Exception;

	ResponseEntity<AccountListDto> createDummyExternalAccounts(AccountDummyDto accountDummyDto) throws Exception;

	ResponseEntity<AccountListDto> findExternalAccountList(String accessToken) throws Exception;

	ResponseEntity<AccountConnectResultDto> connectExternalAccount(String accessToken, int externalAccountId, AccountPasswordDto accountPasswordDto) throws Exception;

}
