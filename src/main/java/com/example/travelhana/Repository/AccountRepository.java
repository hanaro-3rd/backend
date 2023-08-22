package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Projection.AccountInfoProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {

	Optional<Account> findById(int id);

	Boolean existsAccountByAccountNum(String accountNum);

	// QueryDSL 적용하고 각 컬럼만 가져오는 쿼리 짜도록 바꿀 예정
	List<AccountInfoProjection> findAllByUsers_Id(int userId);

}
