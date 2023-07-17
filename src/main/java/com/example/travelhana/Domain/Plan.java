package com.example.travelhana.Domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Plan {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name="PLAN_ID")
    private Long id;

    @ManyToOne
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
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

    @Column
    private Long totalBalance; //디폴트 0


}
