package com.example.travelhana.Domain;

<<<<<<< HEAD
=======
import com.example.travelhana.Dto.Plan.UpdateCategoryBudgetDto;
>>>>>>> 2c922a40dc60536113a5a6cdd329816dc15e42c3
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

<<<<<<< HEAD
	@ColumnDefault("false")
	private boolean isDeleted;

	public void updateCategoryBudget(Long updateCategoryBudget) {
		this.categoryBudget = updateCategoryBudget;
	}

	public void softDeleteCategoryPlan() {
		this.isDeleted = true;
	}
=======
	public void updateCategoryBudget(UpdateCategoryBudgetDto updateCategoryBudgetDto) {
		this.categoryBudget = updateCategoryBudgetDto.getCategoryBudget();
	}
	@ColumnDefault("false")
	private boolean isDeleted;

>>>>>>> 2c922a40dc60536113a5a6cdd329816dc15e42c3

}
