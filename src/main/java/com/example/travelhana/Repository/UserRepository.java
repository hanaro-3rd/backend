package com.example.travelhana.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.travelhana.Domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {


    User findUserByDeviceId(String deviceId);
    List<User> findAll();

}
