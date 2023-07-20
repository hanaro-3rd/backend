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
public class KeyMoney {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name="KEYMONEY_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private User user;
    @Column
    private String unit;

    @Column
    private Long balance;

}
