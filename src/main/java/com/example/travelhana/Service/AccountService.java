package com.example.travelhana.Service;

import com.example.travelhana.Domain.User;
import com.example.travelhana.Repository.ExternalAccountRepository;
import com.example.travelhana.Repository.UserRepository;
import com.example.travelhana.mapper.AccountInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

	private final UserRepository userRepository;
	private final ExternalAccountRepository externalAccountRepository;

	public List<AccountInfoMapper> findExternalAccountList(Long userId) {
		Optional<User> user = userRepository.findById(userId);
		return externalAccountRepository.findAllByRegistrationNum(user.get().getRegistrationNum());
	}

}
