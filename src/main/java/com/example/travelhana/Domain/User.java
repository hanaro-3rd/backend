package com.example.travelhana.Domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name="USER_ID")
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

    @Column(nullable = true)
    private Boolean isWithdrawl; //디폴트 =false

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;



	@Column(nullable = false)
	private String deviceId;

	@Column
	private String salt;

	@Column(nullable = false)
	private Boolean isWithdrawal; //디폴트 =false

}
