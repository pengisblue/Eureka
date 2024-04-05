package com.ssafy.eureka.domain.statistics.service;

import com.ssafy.eureka.common.exception.CustomException;
import com.ssafy.eureka.common.response.ResponseCode;
import com.ssafy.eureka.domain.card.dto.CardBenefitDetailEntity;
import com.ssafy.eureka.domain.card.dto.CardBenefitEntity;
import com.ssafy.eureka.domain.card.repository.CardBenefitDetailRepository;
import com.ssafy.eureka.domain.card.repository.CardBenefitRepository;
import com.ssafy.eureka.domain.card.repository.UserCardRepository;
import com.ssafy.eureka.domain.category.dto.LargeCategoryEntity;
import com.ssafy.eureka.domain.category.dto.SmallCategoryEntity;
import com.ssafy.eureka.domain.category.repository.LargeCategoryRepository;
import com.ssafy.eureka.domain.category.repository.SmallCategoryRepository;
import com.ssafy.eureka.domain.pay.repository.PayHistoryRepository;
import com.ssafy.eureka.domain.statistics.dto.*;
import com.ssafy.eureka.domain.statistics.dto.CardOwnershipDto.BenefitCompareInfo;
import com.ssafy.eureka.domain.statistics.dto.response.*;
import com.ssafy.eureka.domain.statistics.entity.*;
import com.ssafy.eureka.domain.statistics.repository.*;
import com.ssafy.eureka.domain.user.dto.UserInfoDto;
import com.ssafy.eureka.domain.user.repository.UserRepository;
import com.ssafy.eureka.util.DateParserUtil;
import com.ssafy.eureka.util.UserUtil;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final UserCardRepository userCardRepository;
    private final UserRepository userRepository;
    private final ConsumptionStaticRepository consumptionStaticRepository;
    private final ConsumptionLargeStaticRepository consumptionLargeStaticRepository;
    private final DiscountStaticRepository discountStaticRepository;
    private final DiscountLargeStaticRepository discountLargeStaticRepository;
    private final CardOwnershipOverviewRepository cardOwnershipOverviewRepository;
    private final CardOwnershipStaticRepository cardOwnershipStaticRepository;
    private final ConsumptionUserStaticRepository consumptionUserStaticRepository;
    private final CardBenefitDetailRepository cardBenefitDetailRepository;
    private final PayHistoryRepository payHistoryRepository;
    private final CardBenefitRepository cardBenefitRepository;
    private final LargeCategoryRepository largeCategoryRepository;
    private final SmallCategoryRepository smallCategoryRepository;

    private void checkUserCardExistsByUserId(String userId) {
        int parsedUserId = Integer.parseInt(userId);
        if(!userCardRepository.existsByUserId(parsedUserId)) {
            log.debug("보유카드 없음");
            throw new CustomException(ResponseCode.USER_CARD_NOT_FOUND);
        }
    }

    private void checkUserCardExistsByUserCardId(int userCardId) {
        if (!userCardRepository.existsByUserCardId(userCardId)) {
            log.debug("보유카드 없음");
            throw new CustomException(ResponseCode.USER_CARD_NOT_FOUND);
        }
    }

    @Override
    public TotalStatistics totalStatistics(String userId, String yyyyMM){
        // 보유 카드 조회
        checkUserCardExistsByUserId(userId);

        DateParserUtil parser = new DateParserUtil(yyyyMM);
        String year = parser.getYear();
        String month = parser.getMonth();

        BigInteger totalConsumption = consumptionStaticRepository.findTotalConsumptionByUserIdAndDate(Integer.parseInt(userId), year, month);
        Long totalDiscount = discountStaticRepository.findTotalDiscountByUserIdAndDate(Integer.parseInt(userId), year, month);
        BigInteger payApprovedAmt = payHistoryRepository.findPayApprovedAmtByUserIdAndDate(Integer.parseInt(userId), year, month);

        return new TotalStatistics(totalConsumption, totalDiscount, payApprovedAmt);
    }

    @Override
    public ConsumptionStatisticsResponse consumptionStatisticsResponse(String userId, String yyyyMM) {
        // 보유 카드 조회
        checkUserCardExistsByUserId(userId);

        DateParserUtil parser = new DateParserUtil(yyyyMM);
        String year = parser.getYear();
        String month = parser.getMonth();


        BigInteger totalConsumption = consumptionStaticRepository.findTotalConsumptionByUserIdAndDate(Integer.parseInt(userId), year, month);

        List<ConsumptionStatistics> consumptionStatisticsList =
                consumptionLargeStaticRepository.findConsumptionStatisticsByUserIdAndDate(Integer.parseInt(userId), year, month);

        ConsumptionStatisticsResponse response = new ConsumptionStatisticsResponse();
        response.setTotalConsumption(totalConsumption);
        response.setConsumptionList(consumptionStatisticsList);
        return response;
    }

    @Override
    public DiscountStatisticsResponse discountStatisticsResponse(String userId, String yyyyMM) {
        // 보유 카드 조회
        checkUserCardExistsByUserId(userId);

        DateParserUtil parser = new DateParserUtil(yyyyMM);
        String year = parser.getYear();
        String month = parser.getMonth();

        Long totalDiscount = discountStaticRepository.findTotalDiscountByUserIdAndDate(Integer.parseInt(userId), year, month);
        List<DiscountStatistics> discountStatisticsList =
                discountLargeStaticRepository.findDiscountStatisticsByUserIdAndDate(Integer.parseInt(userId), year, month);

        DiscountStatisticsResponse response = new DiscountStatisticsResponse();
        response.setTotalDiscount(totalDiscount);
        response.setDiscountList(discountStatisticsList);
        return response;
    }

    @Override
    public ConsumptionStatisticsResponse consumptionStatisticsByUserCardResponse(int userCardId, String yyyyMM) {
        checkUserCardExistsByUserCardId(userCardId);

        DateParserUtil parser = new DateParserUtil(yyyyMM);
        String year = parser.getYear();
        String month = parser.getMonth();

        Optional<ConsumptionStaticEntity> consumptionStaticEntity =
                consumptionStaticRepository.findByUserCardIdAndYearAndMonth(userCardId, year, month);
        BigInteger totalConsumption = consumptionStaticEntity
                .map(ConsumptionStaticEntity::getTotalConsumption).orElse(BigInteger.ZERO);

        List<ConsumptionStatistics> consumptionStatisticsList =
                consumptionLargeStaticRepository.findConsumptionStatisticsByUserCardIdAndDate(userCardId, year, month);

        ConsumptionStatisticsResponse response = new ConsumptionStatisticsResponse();
        response.setTotalConsumption(totalConsumption);
        response.setConsumptionList(consumptionStatisticsList);
        return response;
    }

    @Override
    public DiscountStatisticsResponse discountStatisticsByUserCardResponse(int userCardId, String yyyyMM) {
        checkUserCardExistsByUserCardId(userCardId);

        DateParserUtil parser = new DateParserUtil(yyyyMM);
        String year = parser.getYear();
        String month = parser.getMonth();

        Optional<DiscountStaticEntity> discountStaticEntity =
                discountStaticRepository.findByUserCardIdAndYearAndMonth(userCardId, year, month);
        Long totalDiscount = Long.valueOf(discountStaticEntity
                .map(DiscountStaticEntity::getTotalDiscount).orElse(0));

        List<Object[]> rawResults =
                discountLargeStaticRepository.findDiscountStatisticsByUserCardIdAndDate(userCardId, year, month);
        List<DiscountStatistics> discountStatisticsList = new ArrayList<>();
        for (Object[] result : rawResults) {
            int largeCategoryId = (int) result[0];
            String categoryName = (String) result[1];
            int discount = (int) result[2];
            discountStatisticsList.add(new DiscountStatistics(largeCategoryId, categoryName, (long) discount));
        }

        DiscountStatisticsResponse response = new DiscountStatisticsResponse();
        response.setTotalDiscount(totalDiscount);
        response.setDiscountList(discountStatisticsList);
        return response;
    }

    @Override
    public BestCardStatisticsResponse bestCardStatisticsResponse(String userId, String yyyyMM) {
        checkUserCardExistsByUserId(userId);

        DateParserUtil parser = new DateParserUtil(yyyyMM);
        String year = parser.getYear();
        String month = parser.getMonth();

        List<BestCardStatistics> bestCardStatisticsList =
                discountStaticRepository.findCardStatisticsByUserIdAndDate(Integer.parseInt(userId), year, month);

        BestCardStatisticsResponse response = new BestCardStatisticsResponse();
        response.setBestCardStatisticsList(bestCardStatisticsList);
        return response;
    }

    @Override
    @Transactional
    public void updateCardOwnershipOverview() {
        List<Object[]> cardOwnershipCounts = userCardRepository.countTotalCardOwnership();

        for (Object[] result : cardOwnershipCounts) {
            int cardId = (int) result[0];
            Long longCount = (Long) result[1];
            int count = longCount.intValue();

            CardOwnershipOverviewEntity newOverview = CardOwnershipOverviewEntity.registerOverview(cardId, count);
            cardOwnershipOverviewRepository.save(newOverview);
        }
    }

    @Override
    @Transactional
    public void updateCardOwnershipStatic() {
        List<UserInfoDto> userList = userRepository.findActiveUserInfo();

        Map<CardOwnershipKey, Integer> ownershipMap = new HashMap<>();

        for (UserInfoDto user : userList) {
            char ageGroup = UserUtil.calculateAgeGroup(user.getUserBirth(), user.getUserGender());
            List<Integer> userCardList = userCardRepository.findCardIdByUserId(user.getUserId());
            int numericGender = Character.getNumericValue(user.getUserGender()) % 2;
            char gender = String.valueOf(numericGender).charAt(0);

            for (Integer userCardId : userCardList) {
                CardOwnershipKey key = new CardOwnershipKey(userCardId, ageGroup, gender);
                ownershipMap.merge(key, 1, Integer::sum);
            }
        }
        ownershipMap.forEach((key, integer) -> {
            CardOwnershipStaticEntity newStatic = CardOwnershipStaticEntity.registerStatic(key.getCardId(),
                    key.getAgeGroup(), key.getGender(), integer);
            cardOwnershipStaticRepository.save(newStatic);
        });
    }

    @Override
    public CardOwnershipResponse cardOwnershipOverviewResponse() {
        LocalDate date = cardOwnershipOverviewRepository.findLatestCreatedDate();

        List<CardOwnershipDto> cardOwnershipOverviewList =
                cardOwnershipOverviewRepository.findCardOwnershipOverviews(date);

        CardOwnershipResponse response = new CardOwnershipResponse();
        response.setSearchInfo(new CardOwnershipResponse.SearchInfo(0,2));
        response.setCardOwnershipList(cardOwnershipOverviewList);
        return response;
    }

    @Override
    public CardOwnershipResponse cardOwnershipStaticResponse(String userId, int userCardId) {
        checkUserCardExistsByUserId(userId);

        LocalDate lastMonth = LocalDate.now().minusMonths(1);

        String yearStr = lastMonth.format(DateTimeFormatter.ofPattern("yyyy"));
        String monthStr = lastMonth.format(DateTimeFormatter.ofPattern("MM"));

        DiscountStaticEntity discountStatic = discountStaticRepository.findByUserCardIdAndYearAndMonth(userCardId, yearStr, monthStr)
            .orElse(null);

        UserInfoDto userInfo = userRepository.findUserInfoByUserId(Integer.parseInt(userId));
        char ageGroup = UserUtil.calculateAgeGroup(userInfo.getUserBirth(), userInfo.getUserGender());

        LocalDate date = cardOwnershipStaticRepository.findLatestCreatedDate();

        List<CardOwnershipDto> cardOwnershipStaticList =
                cardOwnershipStaticRepository.findCardOwnershipStaticByAgeGroup(ageGroup, date);

        for (int i=0; i<cardOwnershipStaticList.size(); i++) {
            int cardId = cardOwnershipStaticList.get(i).getCardId();
            CardBenefitEntity cardBenefitEntity = cardBenefitRepository.findTopByCardId(cardId)
                            .orElseThrow(() -> new CustomException(ResponseCode.CARD_NOT_FOUND));
            String info = cardBenefitEntity.getInfo();
            cardOwnershipStaticList.get(i).setInfo(info);
        }


        for (CardOwnershipDto ownershipStatic : cardOwnershipStaticList) {

            List<CardBenefitEntity> benbefitIdList = cardBenefitRepository.findAllCardBenefitIdsByCardId(ownershipStatic.getCardId());

            List<BenefitCompareInfo> list = new ArrayList<>();

            for(CardBenefitEntity cardBenefit : benbefitIdList){
                List<CardBenefitDetailEntity> cardBenefitDetailList = cardBenefitDetailRepository.findByCardBenefitId(cardBenefit.getCardBenefitId());
                BenefitCompareInfo benefitCompareInfo = new BenefitCompareInfo();

                if(!cardBenefitDetailList.isEmpty()){
                    LargeCategoryEntity largeCategory = largeCategoryRepository.findByLargeCategoryId(cardBenefitDetailList.get(0).getLargeCategoryId());
                    if(largeCategory != null){
                        benefitCompareInfo.setLargeCategoryName(largeCategory.getCategoryName());

                        if(discountStatic != null){
                            DiscountLargeStaticEntity discountLargeStatic = discountLargeStaticRepository.findByDiscountStaticIdAndLargeCategoryId(discountStatic.getDiscountStaticId(), largeCategory.getLargeCategoryId())
                                .orElse(null);

                            if(discountLargeStatic != null){
                                benefitCompareInfo.setBeforeDiscount(discountLargeStatic.getDiscountAmount());
                            }

                            benefitCompareInfo.setAfterDiscount((int)(benefitCompareInfo.getBeforeDiscount() * 1.1));
                            list.add(benefitCompareInfo);
                        }
                    }
                }
            }

            Collections.sort(list);

            ownershipStatic.setBenefitComapareList(list);
        }

        CardOwnershipResponse response = new CardOwnershipResponse();
        response.setCardOwnershipList(cardOwnershipStaticList);
        response.setSearchInfo(new CardOwnershipResponse.SearchInfo(Integer.parseInt(String.valueOf(ageGroup)), 2));
        return response;
    }

    @Override
    @Transactional
    public void updateConsumptionUserStatic() {
        List<UserInfoDto> userList = userRepository.findActiveUserInfo();

        Map<ConsumptionUserStaticKey, BigInteger> consumptionUserStaticMap = new HashMap<>();
        Map<ConsumptionUserStaticKey, Integer> countMap = new HashMap<>();

        for (UserInfoDto user : userList) {
            char ageGroup = UserUtil.calculateAgeGroup(user.getUserBirth(), user.getUserGender());
            int numericGender = Character.getNumericValue(user.getUserGender()) % 2;
            char gender = String.valueOf(numericGender).charAt(0);

            LocalDate lastMonth = LocalDate.now().minusMonths(1);
            String year = lastMonth.format(DateTimeFormatter.ofPattern("yyyy"));
            String month = lastMonth.format(DateTimeFormatter.ofPattern("MM"));

            List<Integer> userCardList = userCardRepository.findUserCardIdByUserId(user.getUserId());

            for (Integer userCardId : userCardList) {
                List<Object[]> statisticsList =
                        consumptionStaticRepository.findStaticByUserCardIdAndYearAndMonth(userCardId, year, month);

                for (Object[] result : statisticsList) {
                    int categoryId = (int) result[0];
                    BigInteger consumptionAmount = (BigInteger) result[1];
                    ConsumptionUserStaticKey key = new ConsumptionUserStaticKey(categoryId, ageGroup, gender, year, month);
                    consumptionUserStaticMap.merge(key, consumptionAmount, BigInteger::add);
                    countMap.merge(key, 1, Integer::sum);
                }
            }
        }

        consumptionUserStaticMap.forEach((key, consumptionAmount) -> {
            // 소비금액 평균 계산
            Integer count = countMap.getOrDefault(key, 0);
            BigInteger consumptionAverage = count > 0 ? consumptionAmount.divide(BigInteger.valueOf(count)) : BigInteger.ZERO;

            ConsumptionUserStaticEntity entity = ConsumptionUserStaticEntity
                    .register(key.getCategoryId(), key.getAgeGroup(), key.getGender(), key.getYear(), key.getMonth(), consumptionAverage);
            consumptionUserStaticRepository.save(entity);
        });
    }

    @Override
    public ConsumptionCompareResponse consumptionCompareResponse(String userId) {
        checkUserCardExistsByUserId(userId);

        UserInfoDto userInfo = userRepository.findUserInfoByUserId(Integer.parseInt(userId));
        char ageGroup = UserUtil.calculateAgeGroup(userInfo.getUserBirth(), userInfo.getUserGender());
        int numericGender = Character.getNumericValue(userInfo.getUserGender()) % 2;
        char gender = String.valueOf(numericGender).charAt(0);

        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        String year = lastMonth.format(DateTimeFormatter.ofPattern("yyyy"));
        String month = lastMonth.format(DateTimeFormatter.ofPattern("MM"));

        List<ConsumptionCompareDto> consumptionCompareList =
                consumptionLargeStaticRepository.findConsumptionComparisonByUserAndPeriod(Integer.parseInt(userId), year, month, ageGroup, gender);

        BigInteger userTotalConsumption = consumptionStaticRepository.findTotalConsumptionByUserIdAndDate(Integer.parseInt(userId), year, month);
        BigInteger averageTotalConsumption = consumptionUserStaticRepository.findTotalConsumptionByUserInfoAndDate(ageGroup, gender, year, month);

        ConsumptionCompareResponse response = new ConsumptionCompareResponse();
        response.setData(consumptionCompareList);
        response.setAge(ageGroup);
        response.setGender(gender);
        response.setUserAmt(userTotalConsumption);
        response.setAnotherAmt(averageTotalConsumption);

        return response;
    }
}
