package com.example.travelhana.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Account {

	@Id
	@Column(name="ACCOUNT_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@JoinColumn(name = "USER_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

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

	public void updateBalance(Long pay)
	{
		this.balance+=pay;
	}
}