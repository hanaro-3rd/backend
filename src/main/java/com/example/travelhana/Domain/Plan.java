package com.example.travelhana.Domain;

import com.example.travelhana.Dto.Plan.UpdatePlanDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Plan {

	@Id
	@Column(name = "PLAN_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID")
	private User user;

	@Column(nullable = false)
	private Long totalBudget; //디폴트 0

	@Column(nullable = false)
	private String title; //디폴트값 설정해주기

	@Column(nullable = false)
	private String country;

	@Column
	private String city;

	@Column(nullable = false)
	private LocalDateTime startDate;

	@Column(nullable = false)
	private LocalDateTime endDate;

	@Column
	private Long totalBalance; //디폴트 0

	@ColumnDefault("false")
	private boolean isDeleted;

	public void updatePlan(UpdatePlanDto updatePlanDto) {
		this.city = updatePlanDto.getCity();
		this.country = updatePlanDto.getCountry();
		this.startDate = updatePlanDto.getStartDate();
		this.endDate = updatePlanDto.getEndDate();
		this.title = updatePlanDto.getTitle();
	}

	public void softDeletePlan() {
		this.isDeleted = true;
	}

}
