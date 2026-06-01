package com.fankeda.repository;

import com.fankeda.entity.FeedbackRecordEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRecordRepository extends JpaRepository<FeedbackRecordEntity, Long> {

    List<FeedbackRecordEntity> findAllByOrderByIdAsc();

    @Query("select max(feedback.id) from FeedbackRecordEntity feedback")
    Long findMaxId();
}
