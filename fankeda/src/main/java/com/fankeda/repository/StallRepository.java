package com.fankeda.repository;

import com.fankeda.entity.StallEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StallRepository extends JpaRepository<StallEntity, Long> {

    List<StallEntity> findAllByOrderByIdAsc();

    @Query("select max(stall.id) from StallEntity stall")
    Long findMaxId();
}
