package com.example.travelhana.Domain;

import com.example.travelhana.Dto.UpdateCategoryBudgetDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class CategoryPlan {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PLAN_ID")
	private Plan plan;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CATEGORY_ID")
	private Category category;

	@Column(nullable = false)
	private Long categoryBudget; //디폴트 0

	@Column(nullable = false)
	private Long categoryBalance; //디폴트 0

	public void updateCategoryBudget(UpdateCategoryBudgetDto updateCategoryBudgetDto) {
		this.categoryBudget = updateCategoryBudgetDto.getCategoryBudget();
		this.categoryBalance = updateCategoryBudgetDto.getCategoryBalance();
	}



}
