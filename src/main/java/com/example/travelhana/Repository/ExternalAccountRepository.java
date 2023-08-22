package com.example.travelhana.Repository;

import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Projection.AccountInfoProjection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExternalAccountRepository extends JpaRepository<ExternalAccount, Integer> {

	Optional<ExternalAccount> findByIdAndIsConnected(int externalAccountId, Boolean isConnected);

	List<AccountInfoProjection> findAllByRegistrationNumAndIsConnected(String registrationNum, Boolean isConnected);
	List<AccountInfoProjection> findAllByPhoneNumAndIsConnected(String phoneNum, Boolean isConnected);

}