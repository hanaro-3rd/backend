package com.example.travelhana.Repository;

import com.example.travelhana.Domain.ExternalAccount;
import com.example.travelhana.Projection.AccountInfoProjection;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExternalAccountRepository extends JpaRepository<ExternalAccount, Integer> {

	List<AccountInfoProjection> findAllByRegistrationNum(String registrationNum);

}