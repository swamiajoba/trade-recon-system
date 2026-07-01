package com.bank.trading.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Entity
@Table(name = "trades")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeId;

    @NotNull
    private Integer counterpartyId;

    @NotNull
    private Integer instrumentId;

    @Positive(message = "quantity must be greater than 0")
    private int quantity;

    @Positive
    private double price;

    @NotNull
    private LocalDateTime tradeDate;

    @NotBlank
    private String tradeType;

    @NotBlank
    private String status;


}

