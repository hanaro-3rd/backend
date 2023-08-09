package com.example.travelhana.Domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;

import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@Column(name = "USER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String registrationNum;

	@Column(nullable = false)
	private String phoneNum;

	@Column(nullable = false)
	private String pattern;

	@ColumnDefault("false")
	private Boolean isWithdrawal; //디폴트 =false

	@ManyToMany
	private final List<Role> roles = new ArrayList<>();

	@Column(nullable = false)
	private String deviceId;

	@Column
	private String salt;

	private String refreshToken;

	public void updateRefreshToken(String newToken) {
		this.refreshToken = newToken;
	}

}
