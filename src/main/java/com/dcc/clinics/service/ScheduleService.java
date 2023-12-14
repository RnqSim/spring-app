package com.dcc.clinics.service;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dcc.clinics.model.Clinic;
import com.dcc.clinics.model.Schedule;
import com.dcc.clinics.repository.ClinicRepository;
import com.dcc.clinics.repository.ScheduleRepository;

@Service
public class ScheduleService {
	private final ScheduleRepository scheduleRepository;
	private final ClinicRepository clinicRepository;
	
	@Autowired
	public ScheduleService(
			ScheduleRepository scheduleRepository, ClinicRepository clinicRepository) {
		this.scheduleRepository = scheduleRepository;
		this.clinicRepository = clinicRepository;
	}
	
	public String addInitialSchedule(Schedule schedule) {

		return null;
	}
	public String saveSchedule(String name, Long doctorUserId, String scheduleDay, Time startTime, Time endTime, Integer slots) {
	    // Find the clinic by name
	    Clinic clinic = clinicRepository.findByName(name);
	    
	    if (clinic == null) {
	        return "Clinic not found for name: " + name;
	    }

	    // Add conditions for restriction here -
	    List<Schedule> existingSchedules = scheduleRepository.findAllByDoctorUserIdAndScheduleDay(doctorUserId, scheduleDay);

	    // Check for intersecting time slots
	    for (Schedule s : existingSchedules) {
	        if ((s.getStartTime().compareTo(endTime) <= 0) && (startTime.compareTo(s.getEndTime()) <= 0)) {
	            return "Schedule for doctor " + doctorUserId + " on " + scheduleDay + " already exists with intersecting time slots";
	        }
	    }

	    Schedule schedule = new Schedule();
	    schedule.setClinicId(clinic.getClinicId()); // Set the clinicId from the found clinic
	    schedule.setDoctorUserId(doctorUserId);
	    schedule.setScheduleDay(scheduleDay);
	    schedule.setStartTime(startTime);
	    schedule.setEndTime(endTime);
	    schedule.setSlots(slots);

	    scheduleRepository.save(schedule);
	    return "Schedule saved to database";
	}


    public String updateSchedule(Long scheduleId, String scheduleDay, Time startTime, Time endTime, Integer slots) {
        Optional<Schedule> existingSchedule = scheduleRepository.findById(scheduleId);
        if (existingSchedule.isPresent()) {
            Schedule schedule = existingSchedule.get();
            schedule.setScheduleDay(scheduleDay);
            schedule.setStartTime(startTime);
            schedule.setEndTime(endTime);
            schedule.setSlots(slots);
            scheduleRepository.save(schedule);
            return "Schedule updated successfully";
        }
        return "Schedule not found";
    }

    public String deleteSchedule(Long scheduleId) {
        Optional<Schedule> existingSchedule = scheduleRepository.findById(scheduleId);
        if (existingSchedule.isPresent()) {
            scheduleRepository.deleteById(scheduleId);
            return "Schedule deleted successfully";
        }
        return "Schedule not found";
    }
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public List<Schedule> searchByDoctorUserId(Long doctorId) {
        return scheduleRepository.findByDoctorUserId(doctorId);
    }
    public List<Schedule> searchByClinicId(Long clinicId) {
        return scheduleRepository.findByClinicId(clinicId);
    }

    public List<Schedule> searchByScheduleDay(String scheduleDay) {
        return scheduleRepository.findByScheduleDay(scheduleDay);
    }

    public Schedule findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElse(null);
    }
    

}
