package com.dcc.clinics.model;


	public class DoctorScheduleDTO {

	    private Long scheduleId;
	    private Long clinicId;
	    private String scheduleDay;
	    private String startTime;
	    private String endTime;
	    private int slots;
	    private Clinic clinic;

	    // Constructors, getters, setters

	    public DoctorScheduleDTO(Long scheduleId, Long clinicId, String scheduleDay, String startTime, String endTime, int slots, Clinic clinic) {
	        this.scheduleId = scheduleId;
	        this.clinicId = clinicId;
	        this.scheduleDay = scheduleDay;
	        this.startTime = startTime;
	        this.endTime = endTime;
	        this.slots = slots;
	        this.clinic = new Clinic(clinic.getClinicId(), clinic.getName(), clinic.getAddress(), clinic.getOfficeNumber(), clinic.getOfficeEmail(), clinic.getHospital());
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

		public String getScheduleDay() {
			return scheduleDay;
		}

		public void setScheduleDay(String scheduleDay) {
			this.scheduleDay = scheduleDay;
		}

		public String getStartTime() {
			return startTime;
		}

		public void setStartTime(String startTime) {
			this.startTime = startTime;
		}

		public String getEndTime() {
			return endTime;
		}

		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}

		public int getSlots() {
			return slots;
		}

		public void setSlots(int slots) {
			this.slots = slots;
		}

		public Clinic getClinic() {
			return clinic;
		}

		public void setClinic(Clinic clinic) {
			this.clinic = clinic;
		}
	
	    
    
}
