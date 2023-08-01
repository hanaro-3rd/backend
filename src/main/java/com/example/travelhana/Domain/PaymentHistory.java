package com.example.travelhana.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class PaymentHistory {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private Long price;

	@Column
	private Long balance;

	@Column
	private String unit;

	@Column
	private String store;

	@Column
	private String category;

	@Column
	private LocalDateTime createdAt;

	@Column
	private Double lat;

	@Column
	private Double lng;

	@Column
	private String address;

	@Column
	private String memo;

	@Column
	private Long userId;

	@Column
	private Long keyId;

	@Column
	private Boolean isSuccess;

}