package com.dcc.clinics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dcc.clinics.model.Patient;
public interface PatientRepository extends JpaRepository<Patient, Long>{
	Patient findByUserId(Long userId);
	List<Patient> findAll(); 
}
