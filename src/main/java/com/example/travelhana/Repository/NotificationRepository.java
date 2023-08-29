package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
}
