package com.fankeda.repository;

import com.fankeda.entity.AuthTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthTokenRepository extends JpaRepository<AuthTokenEntity, String> {
}
