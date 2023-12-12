package com.dcc.clinics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dcc.clinics.model.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>{
	Schedule findByScheduleId(Long ScheduleId);
	Schedule findByClinicId(Long ClinicId);
	List<Schedule> findByScheduleDay(String scheduleDay);
	List<Schedule> findByDoctorUserId(Long doctorUserId);
	List<Schedule> findAllByDoctorUserIdAndScheduleDay(Long doctorUserId, String scheduleDay);


}
