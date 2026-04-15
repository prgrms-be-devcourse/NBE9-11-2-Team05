package com.team05.demo.domain.user.refreshtoken.repository;

import com.team05.demo.domain.user.refreshtoken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
