package com.bank.trading.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Entity
@Table(name = "counterparties")
public class Counterparty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cpId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, length = 20)
    private String lei;
    private String bic;
    private String country;
    private String creditRating;
    private String counterpartyType;


}
