package com.ssafy.eureka.domain.pay.dto.response;

import com.ssafy.eureka.domain.pay.dto.PayHistoryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayHistoryListResponse {

    private String cardName;
    private int payHistoryId;
    private String orderId;
    private int userId;
    private int userCardId;
    private int recommendCardId;
    private String approvedNum;
    private LocalDateTime approvedDateTime;
    private int approvedAmt;
    private int status;
    private Integer modifiedAmt;
    private int totalInstallCnt;
    private int discount;
    private int recommendDiscount;
    private String day;

    private String largeCategoryName;
    private String smallCategoryName;

    public PayHistoryListResponse(String cardName, PayHistoryEntity entity, String largeCategoryName, String smallCategoryName){

        this.cardName = cardName;
        this.payHistoryId = entity.getPayHistoryId();
        this.orderId = entity.getOrderId();
        this.userId = entity.getUserId();
        this.userCardId = entity.getUserCardId();
        this.recommendCardId = entity.getRecommendCardId();
        this.approvedNum = entity.getApprovedNum();
        this.approvedDateTime = entity.getApprovedDateTime();
        this.approvedAmt  = entity.getApprovedAmt();
        this.status = entity.getStatus();
        this.modifiedAmt = entity.getModifiedAmt();
        this.totalInstallCnt = entity.getTotalInstallCnt();
        this.discount = entity.getDiscount();
        this.recommendDiscount = entity.getRecommendDiscount();
        String date = String.valueOf(entity.getApprovedDateTime());
        this.day = date.substring(8, 10);

        this.largeCategoryName = largeCategoryName;
        this.smallCategoryName = smallCategoryName;
    }
}
