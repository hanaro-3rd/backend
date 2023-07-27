package com.example.travelhana.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.travelhana.Domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {


    Optional<User> findByDeviceId(String deviceId);
    boolean existsByDeviceId(String deviceId);

}

