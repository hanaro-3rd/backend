package com.example.travelhana.Domain;

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
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name="USER_ID")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String registrationNum;

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private String pattern;

    @Column(nullable = false)
    private String deviceId;

    @Column
    private String salt;

    @Column(nullable = false)
    private Boolean isWithdrawl; //디폴트 =false


}
