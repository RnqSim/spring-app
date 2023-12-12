package com.dcc.clinics.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "clinic")
public class Clinic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clinic_id")
    private Long clinicId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false, length = 250)
    private String address;

    @Column(name = "office_number", nullable = false, length = 20)
    private String officeNumber;

    @Column(name = "office_email", nullable = false, length = 50)
    private String officeEmail;

    @Column(name = "hospital", length = 255)
    private String hospital;
    
    public Clinic() {
    	
    }
	public Clinic(Long clinicId, String name, String address, String officeNumber, String officeEmail,
			String hospital) {
		super();
		this.clinicId = clinicId;
		this.name = name;
		this.address = address;
		this.officeNumber = officeNumber;
		this.officeEmail = officeEmail;
		this.hospital = hospital;
	}
	public Long getClinicId() {
		return clinicId;
	}
	public void setClinicId(Long clinicId) {
		this.clinicId = clinicId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getOfficeNumber() {
		return officeNumber;
	}
	public void setOfficeNumber(String officeNumber) {
		this.officeNumber = officeNumber;
	}
	public String getOfficeEmail() {
		return officeEmail;
	}
	public void setOfficeEmail(String officeEmail) {
		this.officeEmail = officeEmail;
	}
	public String getHospital() {
		return hospital;
	}
	public void setHospital(String hospital) {
		this.hospital = hospital;
	}
    
    
}
