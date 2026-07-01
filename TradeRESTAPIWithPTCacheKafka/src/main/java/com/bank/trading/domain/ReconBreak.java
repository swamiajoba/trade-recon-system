package com.bank.trading.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Entity
@Table(name = "recon_breaks")
public class ReconBreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer breakId;

    private Long tradeId;
    private String breakType;
    private Integer ourQty;
    private Integer theirQty;
    private Double ourAmount;
    private Double theirAmount;
    private String status;

    // Added new
    private String resolvedBy;


}
