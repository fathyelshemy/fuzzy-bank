package com.blueharvest.bank.entities;

import com.blueharvest.bank.dto.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private long id;

    @Column(name = "amount",nullable = false)
    private double amount;

    @Column(name = "transaction_type",nullable = false)
    private TransactionType transactionType;

    @OneToOne(cascade = CascadeType.ALL)
    private SubAccount subAccountId;

}
