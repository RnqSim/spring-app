package com.dcc.clinics.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dcc.clinics.model.Clinic;

public interface ClinicRepository extends JpaRepository<Clinic, Long>{
	Clinic findByClinicId(Long ClinicId);

	List<Clinic> findByAddressContaining(String address);

	List<Clinic> findByNameContaining(String name);

	Clinic findByName(String name);
}
