package com.dcc.clinics.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "doctors")
public class Doctor {
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
    
    @Column(name = "secretary", length = 255)
    private String secretary;

	@Column(name = "license_number", length = 255)
	private String licenseNumber;

	@Column(name = "ptr_number", length = 255)
	private String ptrNumber;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

	
	public Doctor() {
	}

	public Doctor(UnverifiedDoctor unvDoctor) {
		this.userId = unvDoctor.getUserId();
		this.prcId = unvDoctor.getPrcId();
		this.specialization = unvDoctor.getSpecialization();
		this.credentials = unvDoctor.getCredentials();
		this.approvalStatus = unvDoctor.getApprovalStatus();
		this.licenseNumber = unvDoctor.getLicenseNumber();
		this.ptrNumber = unvDoctor.getPtrNumber();
	}

	public Doctor(Long userId, String prcId, String specialization, String credentials, String approvalStatus,
			User user, String secretary, String licenseNumber, String ptrNumber) {
		super();
		this.userId = userId;
		this.prcId = prcId;
		this.specialization = specialization;
		this.credentials = credentials;
		this.approvalStatus = approvalStatus;
		this.user = user;
		this.secretary = secretary;
		this.licenseNumber = licenseNumber;
		this.ptrNumber = ptrNumber;
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

	public String getSecretary() {
		return secretary;
	}

	public void setSecretary(String secretary) {
		this.secretary = secretary;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


	
	

	
}
