package com.dcc.clinics.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dcc.clinics.model.UnverifiedDoctor;

public interface UnverifiedDoctorRepository extends JpaRepository<UnverifiedDoctor, Long>{
	UnverifiedDoctor findByUserId(Long userId);
}
