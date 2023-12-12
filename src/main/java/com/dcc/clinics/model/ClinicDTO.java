package com.dcc.clinics.model;

public class ClinicDTO {

    private Long clinicId;
    private String name;
    private String address;
    private String officeNumber;
    private String officeEmail;
    private String hospital;

    
    public ClinicDTO(Long clinicId, String name, String address, String officeNumber, String officeEmail, String hospital) {
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
