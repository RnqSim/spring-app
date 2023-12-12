package com.dcc.clinics.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dcc.clinics.model.UnverifiedUser;

public interface UnverifiedUserRepository extends JpaRepository<UnverifiedUser, Long>{
	UnverifiedUser findByUsername(String username);
	UnverifiedUser findByEmail(String email);
	UnverifiedUser findByEmailAndOtp(String email, Integer otp);
	UnverifiedUser findByUserId(Long userId);
}
