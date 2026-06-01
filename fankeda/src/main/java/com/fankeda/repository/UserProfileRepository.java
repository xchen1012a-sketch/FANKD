package com.fankeda.repository;

import com.fankeda.entity.UserProfileEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {

    List<UserProfileEntity> findAllByOrderByIdAsc();

    Optional<UserProfileEntity> findByOpenid(String openid);

    @Query("select max(user.id) from UserProfileEntity user")
    Long findMaxId();
}
