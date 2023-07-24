package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Domain.KeyMoney;
import com.example.travelhana.Projection.AccountInfoProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {



	Boolean existsAccountByAccountNum(String accountNum);

	List<AccountInfoProjection> findAllByUser_Id(int userId);

}
