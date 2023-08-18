package com.example.travelhana.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class MarkerHistory {

	@Id
	@Column(name = "MARKER_HISTORY_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private int userId;

	@Column
	private int markerId;

	@Column
	private int keymoneyId;

	@Column
	private LocalDateTime pickDate;

	@Column
	private Long amount;

	@Column
	private Long balance;

	@Column
	private String place;

}