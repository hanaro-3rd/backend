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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name="ACCOUNT_ID")
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

    public void updateBalance(Long pay)
    {
        this.balance+=pay;
    }
	@Column
	private String salt;

	@Column(nullable = false)
	private Long balance;

	public Account(User user, String accountNum, String bank, Date openDate, String password, String salt, Long balance) {
		this.user = user;
		this.accountNum = accountNum;
		this.bank = bank;
		this.openDate = openDate;
		this.password = password;
		this.salt = salt;
		this.balance = balance;
	}


}