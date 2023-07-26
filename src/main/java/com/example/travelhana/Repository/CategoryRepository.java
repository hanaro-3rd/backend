package com.example.travelhana.Repository;

import com.example.travelhana.Domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> { //JPARepository<테이블명,pk> => findById같은 메소드 기본 제공

}
