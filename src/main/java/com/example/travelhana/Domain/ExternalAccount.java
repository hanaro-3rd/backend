package com.example.travelhana.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ExternalAccount {

	@Id
	@Column(name = "EXTERNAL_ACCOUNT_ID")
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

	@Builder.Default
	private Long balance=10000000L;

	@Column(nullable = false)
	private String registrationNum;

	@Builder.Default
	private Boolean isConnected=false;

	public void changeConnectionStatus() {
		this.isConnected = true;
	}

}
