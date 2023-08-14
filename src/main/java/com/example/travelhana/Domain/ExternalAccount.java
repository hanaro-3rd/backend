package com.example.travelhana.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ExternalAccount {

	@Id
	@Column(name = "EXTERNALACCOUNT_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

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

	@Column(nullable = false)
	private String registrationNum;

	@ColumnDefault("false")
	private Boolean isConnected;

	public void changeConnectionStatus() {
		this.isConnected = true;
	}

}
