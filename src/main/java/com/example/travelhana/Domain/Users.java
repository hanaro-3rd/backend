package com.example.travelhana.Domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.List;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Users {

	@Id
	@Column(name = "USER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String pattern;

	@Column(nullable = false)
	private String registrationNum;

	@Column(nullable = false)
	private String phoneNum;

	@Builder.Default
	private Boolean isWithdrawal = false; //디폴트 =false

	@ManyToMany
	private final List<Role> roles = new ArrayList<>();

	@Column(nullable = true)
	private String deviceId;

	@Column
	private String salt;

	@Column
	private String refreshToken;

	@Column
	@CreationTimestamp
	private final LocalDateTime createdAt = LocalDateTime.now();

	public void updateRefreshToken(String newToken) {
		this.refreshToken = newToken;
	}

	public void updatePassword(String password) {
		this.password = password;
	}
	public void updateDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}


}
