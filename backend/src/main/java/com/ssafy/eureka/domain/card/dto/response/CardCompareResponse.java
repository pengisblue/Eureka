package com.ssafy.eureka.domain.card.dto.response;

import com.ssafy.eureka.domain.card.dto.CardEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CardCompareResponse {

    CompareCard myCard;
    CompareCard categoryCard;
    CompareCard ddoraeCard;

    public CardCompareResponse(CardEntity entity1, int discount, CardEntity entity2, int discount2, CardEntity entity3, int discount3){
        this.myCard = new CompareCard();
        this.myCard.cardId = entity1.getCardId();
        this.myCard.cardCompanyId = entity1.getCardCompanyId();
        this.myCard.cardType = entity1.getCardType();
        this.myCard.cardName = entity1.getCardName();
        this.myCard.annualFee = entity1.getAnnualFee();
        this.myCard.previousPerformance = entity1.getPreviousPerformance();
        this.myCard.imagePath = entity1.getImagePath();
        this.myCard.imgAttr = entity1.getImgAttr();
        this.myCard.joinPath = entity1.getJoinPath();
        this.myCard.discount = discount;

        this.categoryCard = new CompareCard();
        this.categoryCard.cardId = entity2.getCardId();
        this.categoryCard.cardCompanyId = entity2.getCardCompanyId();
        this.categoryCard.cardType = entity2.getCardType();
        this.categoryCard.cardName = entity2.getCardName();
        this.categoryCard.annualFee = entity2.getAnnualFee();
        this.categoryCard.previousPerformance = entity2.getPreviousPerformance();
        this.categoryCard.imagePath = entity2.getImagePath();
        this.categoryCard.imgAttr = entity2.getImgAttr();
        this.categoryCard.joinPath = entity2.getJoinPath();
        this.categoryCard.discount = discount2;

        this.ddoraeCard = new CompareCard();
        this.ddoraeCard.cardId = entity3.getCardId();
        this.ddoraeCard.cardCompanyId = entity3.getCardCompanyId();
        this.ddoraeCard.cardType = entity3.getCardType();
        this.ddoraeCard.cardName = entity3.getCardName();
        this.ddoraeCard.annualFee = entity3.getAnnualFee();
        this.ddoraeCard.previousPerformance = entity3.getPreviousPerformance();
        this.ddoraeCard.imagePath = entity3.getImagePath();
        this.ddoraeCard.imgAttr = entity3.getImgAttr();
        this.ddoraeCard.joinPath = entity3.getJoinPath();
        this.ddoraeCard.discount = discount3;
    }
}

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
class CompareCard{
   int cardId;
   int cardCompanyId;
   int cardType;
   String cardName;
   int annualFee;
   int previousPerformance;
   String imagePath;
   int imgAttr;
   String joinPath;
   int discount;
}
