package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Projection.AccountInfoProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

	Boolean existsAccountByAccountNum(String accountNum);

	List<AccountInfoProjection> findAllByUser_Id(Long userId);

}