package com.example.travelhana.Service;

import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Repository.ExternalAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalAccountService {

	private final ExternalAccountRepository externalAccountRepository;

	public Boolean addExternalAccount(ExternalAccountDto externalAccountDto) {

	}

}
