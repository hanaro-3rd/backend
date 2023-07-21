package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Account;
import com.example.travelhana.Domain.KeyMoney;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

public interface AccountRepository extends JpaRepository<Account, Integer> {


}
