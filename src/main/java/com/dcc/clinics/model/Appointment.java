package com.dcc.clinics.model;
import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_no")
    private Long transactionNo;

    @Column(name = "patient_user_id")
    private Long patientUserId;

    @Column(name = "doctor_user_id")
    private Long doctorUserId;

    @Column(name = "clinic_id")
    private Long clinicId;

    @Column(name = "clinic_name", nullable = false, length = 255)
    private String clinicName;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "patient_name", nullable = false, length = 120)
    private String patientName;

    @Column(name = "doctor_name", nullable = false, length = 120)
    private String doctorName;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "schedule_day", nullable = false, length = 20)
    private String scheduleDay;

    @Column(name = "schedule_date", nullable = false)
    private Date scheduleDate;

    @Column(name = "start_time", nullable = false)
    private Time startTime;

    @Column(name = "end_time", nullable = false)
    private Time endTime;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

	@Column(name = "slots", nullable = false)
	private Integer slots;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patient_user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User patientUser;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "doctor_user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User doctorUser;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "clinic_id", insertable=false, updatable=false)
    private Clinic clinic;

    // constructors, getters, and setters
    public Appointment() {
    }

	public Appointment(Long transactionNo, Long patientUserId, Long doctorUserId, Long clinicId, String clinicName,
			String address, String patientName, String doctorName, Long scheduleId, String scheduleDay,
			Date scheduleDate, Time startTime, Time endTime, String status, Integer slots, User patientUser, User doctorUser,
			Clinic clinic) {
		super();
		this.transactionNo = transactionNo;
		this.patientUserId = patientUserId;
		this.doctorUserId = doctorUserId;
		this.clinicId = clinicId;
		this.clinicName = clinicName;
		this.address = address;
		this.patientName = patientName;
		this.doctorName = doctorName;
		this.scheduleId = scheduleId;
		this.scheduleDay = scheduleDay;
		this.scheduleDate = scheduleDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.slots = slots;
		this.patientUser = patientUser;
		this.doctorUser = doctorUser;
		this.clinic = clinic;
	}

	public Integer getSlots() {
		return slots;
	}

	public void setSlots(Integer slots) {
		this.slots = slots;
	}

	public Long getTransactionNo() {
		return transactionNo;
	}

	public void setTransactionNo(Long transactionNo) {
		this.transactionNo = transactionNo;
	}

	public Long getPatientUserId() {
		return patientUserId;
	}

	public void setPatientUserId(Long patientUserId) {
		this.patientUserId = patientUserId;
	}

	public Long getDoctorUserId() {
		return doctorUserId;
	}

	public void setDoctorUserId(Long doctorUserId) {
		this.doctorUserId = doctorUserId;
	}

	public Long getClinicId() {
		return clinicId;
	}

	public void setClinicId(Long clinicId) {
		this.clinicId = clinicId;
	}

	public String getClinicName() {
		return clinicName;
	}

	public void setClinicName(String clinicName) {
		this.clinicName = clinicName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}

	public String getScheduleDay() {
		return scheduleDay;
	}

	public void setScheduleDay(String scheduleDay) {
		this.scheduleDay = scheduleDay;
	}

	public Date getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(Date scheduleDate) {
		this.scheduleDate = scheduleDate;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getPatientUser() {
		return patientUser;
	}

	public void setPatientUser(User patientUser) {
		this.patientUser = patientUser;
	}

	public User getDoctorUser() {
		return doctorUser;
	}

	public void setDoctorUser(User doctorUser) {
		this.doctorUser = doctorUser;
	}

	public Clinic getClinic() {
		return clinic;
	}

	public void setClinic(Clinic clinic) {
		this.clinic = clinic;
	}
    
}