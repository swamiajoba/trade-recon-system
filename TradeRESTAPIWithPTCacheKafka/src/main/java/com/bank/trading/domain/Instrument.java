package com.bank.trading.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Entity
@Table(name = "instruments")
public class Instrument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer instrumentId;

    private String isin;
    private String assetClass;
    private String instrumentName;
    private String currency;
    private String issuer;
    private LocalDate maturityDate;
    private Double couponRate;


}
