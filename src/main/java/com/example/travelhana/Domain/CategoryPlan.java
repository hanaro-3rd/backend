package com.example.travelhana.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class CategoryPlan {

	@Id
	@Column(name = "CATEGORY_PLAN_ID")
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

	@ColumnDefault("false")
	private boolean isDeleted;

	public void updateCategoryBudget(Long updateCategoryBudget) {
		this.categoryBudget = updateCategoryBudget;
	}

	public void softDeleteCategoryPlan() {
		this.isDeleted = true;
	}

}
