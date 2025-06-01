package com.restaurant.User_service.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.restaurant.User_service.Entity.VerificationCode;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    
    Optional<VerificationCode> findByEmailAndVerificationCode(String email, String token);
    
    Optional<VerificationCode> findTopByEmailOrderByExpiryDateDesc(String email);
    
    List<VerificationCode> findAllByEmail(String email);
}