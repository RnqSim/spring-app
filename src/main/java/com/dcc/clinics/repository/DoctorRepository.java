package com.dcc.clinics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dcc.clinics.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
	Doctor findByUserId(Long userId);
	List<Doctor> findAll(); 

}
