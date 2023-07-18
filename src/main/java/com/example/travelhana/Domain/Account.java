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
public class Account {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name="ACCOUNT_ID")
    private int id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user; //fetch= LAZY

    @Column (nullable = false)
    private String accountNum;

    @Column(nullable = false)
    private Date openDate;

    @Column(nullable = false)
    private String password;

    @Column
    private String salt;

    @Column(nullable = false)
    private Long balance;


}