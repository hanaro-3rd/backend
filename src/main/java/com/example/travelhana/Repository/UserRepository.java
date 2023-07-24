package com.example.travelhana.Repository;

import com.example.travelhana.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}