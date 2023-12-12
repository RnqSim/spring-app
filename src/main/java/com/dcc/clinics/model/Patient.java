package com.dcc.clinics.model;

import jakarta.persistence.*;

@Entity
@Table(name = "patients")
public class Patient{
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
	    private User user;
	    
	public Patient() {
		
	}
	
	public Patient(UnverifiedPatient unvPat) {
		this.userId = unvPat.getUserId();
		this.seniorId =  unvPat.getSeniorId();
		this.pwdId =  unvPat.getPwdId();
		this.philhealthId =  unvPat.getPhilhealthId();
		this.hmo =  unvPat.getHmo();
	}


	public Patient(Long userId, String seniorId, String pwdId, String philhealthId, String hmo, User user) {
		super();
		this.userId = userId;
		this.seniorId = seniorId;
		this.pwdId = pwdId;
		this.philhealthId = philhealthId;
		this.hmo = hmo;
		this.user = user;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	

}
