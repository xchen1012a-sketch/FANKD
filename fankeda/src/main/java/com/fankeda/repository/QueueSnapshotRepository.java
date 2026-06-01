package com.fankeda.repository;

import com.fankeda.entity.QueueSnapshotEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QueueSnapshotRepository extends JpaRepository<QueueSnapshotEntity, Long> {

    Optional<QueueSnapshotEntity> findTopByStallIdOrderByCreatedAtDescIdDesc(Long stallId);

    @Query("select max(snapshot.id) from QueueSnapshotEntity snapshot")
    Long findMaxId();
}
