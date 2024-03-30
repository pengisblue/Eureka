package com.ssafy.eureka.domain.statistics.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "consumption_large_static")
public class ConsumptionLargeStaticEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int consumptionLargeStaticId;

    @NotNull
    private int consumptionStaticId;

    @NotNull
    private int largeCategoryId;

    @NotNull
    @Column(columnDefinition = "BIGINT")
    private BigInteger consumptionAmount;

    @NotNull
    private int consumptionCount;

    public ConsumptionLargeStaticEntity(int consumptionStaticId, int largeCategoryId) {
        this.consumptionStaticId = consumptionStaticId;
        this.largeCategoryId = largeCategoryId;
        this.consumptionAmount = BigInteger.ZERO;
        this.consumptionCount = 0;
    }

    public void addPay(int totalAmount) {
        this.consumptionAmount.add(BigInteger.valueOf(totalAmount));
        this.consumptionCount += 1;
    }
}
