package com.dcc.clinics.service;

import com.dcc.clinics.model.Clinic;
import com.dcc.clinics.repository.ClinicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClinicService {
    private final ClinicRepository clinicRepository;

    @Autowired
    public ClinicService(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }
    
    

    public List<Clinic> getAllClinics() {
        return clinicRepository.findAll();
    }

    public Clinic findByName(String name) {
        return clinicRepository.findByName(name);
    }
    public List<Clinic> searchByName(String name) {
        return clinicRepository.findByNameContaining(name);
    }

    public List<Clinic> findByAddressContaining(String address) {
        return clinicRepository.findByAddressContaining(address);
    }

    public String saveClinic(Clinic clinic) {
        // Add conditions for restriction here -
        // if same name :
        Clinic existingClinic = clinicRepository.findByName(clinic.getName());

        if (existingClinic == null) {
            // If no clinic with the same name exists, save the new clinic
            clinicRepository.save(clinic);
            return "Clinic saved to database";
        } else {
            return "Clinic with the same name already exists";
        }
    }

 // Assuming you have a DTO (Data Transfer Object) to represent the updated clinic details
    public String updateClinic(Long clinicId, Clinic updatedClinic) {
        // Retrieve the clinic from the repository
        Clinic clinic = clinicRepository.findByClinicId(clinicId);

        if (clinic != null) {
            // Update the clinic properties with the values from the provided clinic
            clinic.setName(updatedClinic.getName());
            clinic.setAddress(updatedClinic.getAddress());
            clinic.setOfficeNumber(updatedClinic.getOfficeNumber());
            clinic.setOfficeEmail(updatedClinic.getOfficeEmail());
            clinic.setHospital(updatedClinic.getHospital());

            // Save the updated clinic
            clinicRepository.save(clinic);

            return "Clinic updated successfully";
        } else {
            return "Clinic not found";
        }
    }



    public String deleteClinic(Long id) {
        clinicRepository.deleteById(id);
        return "Clinic deleted";
    }

    public Clinic findClinicById(Long clinicId) {
        return clinicRepository.findById(clinicId).orElse(null);
    }

}
