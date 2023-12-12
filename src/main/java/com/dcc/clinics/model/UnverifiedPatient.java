package com.dcc.clinics.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "unverified_patients")
public class UnverifiedPatient{
	@Id
    @Column(name = "user_id")
    private Long userId;

	@Column(name = "senior_id", length = 255)
    private String seniorId;

    @Column(name = "pwd_id", length = 15)
    private String pwdId;

    @Column(name = "philhealth_id", length = 12)
    private String philhealthId;

    @Column(name = "hmo", length = 255)
    private String hmo;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UnverifiedUser unvUser;
	
	
	public UnverifiedPatient() {
	}
	
	public UnverifiedPatient(Patient patient) {
		this.userId = patient.getUserId();
		this.seniorId = patient.getSeniorId();
		this.pwdId = patient.getPwdId();
		this.philhealthId = patient.getPhilhealthId();
		this.hmo = patient.getHmo();
	}

	public UnverifiedPatient(Long userId, String seniorId, String pwdId, String philhealthId, String hmo,
			UnverifiedUser unvUser) {
		super();
		this.userId = userId;
		this.seniorId = seniorId;
		this.pwdId = pwdId;
		this.philhealthId = philhealthId;
		this.hmo = hmo;
		this.unvUser = unvUser;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getSeniorId() {
		return seniorId;
	}

	public void setSeniorId(String seniorId) {
		this.seniorId = seniorId;
	}

	public String getPwdId() {
		return pwdId;
	}

	public void setPwdId(String pwdId) {
		this.pwdId = pwdId;
	}

	public String getPhilhealthId() {
		return philhealthId;
	}

	public void setPhilhealthId(String philhealthId) {
		this.philhealthId = philhealthId;
	}

	public String getHmo() {
		return hmo;
	}

	public void setHmo(String hmo) {
		this.hmo = hmo;
	}

	public UnverifiedUser getUnvUser() {
		return unvUser;
	}

	public void setUnvUser(UnverifiedUser unvUser) {
		this.unvUser = unvUser;
	}

	
}
