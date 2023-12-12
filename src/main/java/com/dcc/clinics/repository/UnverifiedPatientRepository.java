package com.dcc.clinics.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dcc.clinics.model.UnverifiedPatient;

public interface UnverifiedPatientRepository extends JpaRepository<UnverifiedPatient, Long>{
	UnverifiedPatient findByUserId(Long userId);
}
