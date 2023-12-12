package com.dcc.clinics.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "unverified_doctors")
public class UnverifiedDoctor {
	@Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "prc_id", nullable = false, length = 255)
    private String prcId;

    @Column(name = "spec", nullable = false, length = 50)
    private String specialization;

    @Column(name = "credentials", length = 50)
    private String credentials;

    @Column(name = "approval_status", length = 255)
    private String approvalStatus;
    
    @Column(name = "license_number", length = 255)
	private String licenseNumber;

	@Column(name = "ptr_number", length = 255)
	private String ptrNumber;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private UnverifiedUser unvUser;
	
	
	public UnverifiedDoctor() {
	}
	
	public UnverifiedDoctor(Doctor doctor) {
		this.userId = doctor.getUserId();
		this.prcId = doctor.getPrcId();
		this.specialization = doctor.getSpecialization();
		this.credentials = doctor.getCredentials();
		this.approvalStatus = doctor.getApprovalStatus();
		this.licenseNumber = getLicenseNumber();
		this.ptrNumber = getPtrNumber();
		
	}

	public UnverifiedDoctor(Long userId, String prcId, String specialization, String credentials, String approvalStatus, String licenseNumber, String ptrNumber,
			UnverifiedUser unvUser) {
		super();
		this.userId = userId;
		this.prcId = prcId;
		this.specialization = specialization;
		this.credentials = credentials;
		this.approvalStatus = approvalStatus;
		this.licenseNumber = licenseNumber;
		this.ptrNumber = ptrNumber;
		this.unvUser = unvUser;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPrcId() {
		return prcId;
	}

	public void setPrcId(String prcId) {
		this.prcId = prcId;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public String getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(String approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public UnverifiedUser getUnvUser() {
		return unvUser;
	}

	public void setUnvUser(UnverifiedUser unvUser) {
		this.unvUser = unvUser;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public String getPtrNumber() {
		return ptrNumber;
	}

	public void setPtrNumber(String ptrNumber) {
		this.ptrNumber = ptrNumber;
	}
	
	
	

	
}
