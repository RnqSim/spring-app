package com.dcc.clinics.repository;

import com.dcc.clinics.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByScheduleDay(String scheduleDay);

    List<Appointment> findAllByScheduleId(Long scheduleUserId);

	List<Appointment> findAllByPatientUserId(Long patientUserId);

	List<Appointment> findAllByDoctorUserId(Long doctorUserId);
}
