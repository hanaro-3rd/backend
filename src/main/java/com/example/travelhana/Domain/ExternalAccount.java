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
public class ExternalAccount {

    @Id
    @Column(name="EXTERNAL_ACCOUNT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(nullable = false)
    private Long balance;

    @Column(nullable = false)
    private String registrationNum;

}