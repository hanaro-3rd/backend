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

    @Id
    @Column(name="KEYMONEY_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    private User user;

    @Column
    private String unit;

    @Column
    private Long balance;

    public void updatePlusBalance(Long amount) {
        this.balance += amount;
    }
    public void updateMinusBalance(Long amount) {this.balance -= amount;}

}