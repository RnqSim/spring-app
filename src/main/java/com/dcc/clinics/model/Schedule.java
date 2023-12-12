package com.dcc.clinics.model;

import java.sql.Time;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @Column(name = "clinic_id")
    private Long clinicId;

    @Column(name = "doctor_user_id")
    private Long doctorUserId;

    @Column(name = "schedule_day", length = 255)
    private String scheduleDay;

    @Column(name = "start_time")
    private Time startTime;

    @Column(name = "end_time")
    private Time endTime;

    @Column(name = "slots")
    private Integer slots;

    @ManyToOne
    @JoinColumn(name = "clinic_id", insertable=false, updatable=false)
    private Clinic clinic;

    @ManyToOne
    @JoinColumn(name = "doctor_user_id", referencedColumnName = "user_id", insertable=false, updatable=false)
    private Doctor doctor;
    
    public Schedule() {
    	
    }
    
	public Schedule(Long scheduleId, Long clinicId, Long doctorUserId, String scheduleDay, Time startTime, Time endTime,
			Integer slots, Clinic clinic, Doctor doctor) {
		super();
		this.scheduleId = scheduleId;
		this.clinicId = clinicId;
		this.doctorUserId = doctorUserId;
		this.scheduleDay = scheduleDay;
		this.startTime = startTime;
		this.endTime = endTime;
		this.slots = slots;
		this.clinic = clinic;
		this.doctor = doctor;
	}

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}

	public Long getClinicId() {
		return clinicId;
	}

	public void setClinicId(Long clinicId) {
		this.clinicId = clinicId;
	}

	public Long getDoctorUserId() {
		return doctorUserId;
	}

	public void setDoctorUserId(Long doctorUserId) {
		this.doctorUserId = doctorUserId;
	}

	public String getScheduleDay() {
		return scheduleDay;
	}

	public void setScheduleDay(String scheduleDay) {
		this.scheduleDay = scheduleDay;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public Integer getSlots() {
		return slots;
	}

	public void setSlots(Integer slots) {
		this.slots = slots;
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	
    
}