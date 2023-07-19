package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Domain.ExternalAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExternalAccountRepository extends JpaRepository<ExternalAccount,Long> {

	List<Account> findAccountsByRegistrationNum(String RegistrationNum);

}