package com.example.travelhana.Domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Account {

	@Id
	@Column(name = "ACCOUNT_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@JoinColumn(name = "USER_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private Users users;

	@Column(nullable = false)
	private String accountNum;

	@Column(nullable = false)
	private String bank;

	@Column(nullable = false)
	private Date openDate;

	@Column(nullable = false)
	private String password;

	@Column
	private String salt;

	@Column(nullable = false)
	private Long balance;

	public void updateBalance(Long pay) {
		this.balance += pay;
	}

}