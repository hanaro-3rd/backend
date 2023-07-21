package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {

	Boolean existsAccountByAccountNum(String accountNum);

}