package com.ssafy.card.Card.repository;

import com.ssafy.card.Card.entity.CardHistoryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CardHistoryRepository extends JpaRepository<CardHistoryEntity, String> {

    @Query(value =
          "SELECT * "
        + "FROM card_history c "
        + "WHERE c.user_card_id = :userCardId "
        + "AND YEAR(c.approved_date_time) = :year "
        + "AND MONTH(c.approved_date_time) = :month", nativeQuery = true)
    List<CardHistoryEntity> findByUserCardIdAndMonthAndYear(@Param("userCardId") int userCardId, @Param("year") String year, @Param("month") String month);

}