package com.ssafy.eureka.domain.card.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "benefit_detail")
public class CardBenefitDetailEntity {
    @Id
    private int cardBenefitDetailId;
}
