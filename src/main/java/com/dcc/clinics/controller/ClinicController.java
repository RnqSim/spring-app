package com.dcc.clinics.controller;

import com.dcc.clinics.model.Clinic;
import com.dcc.clinics.service.ClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://docclickconnect.vercel.app/")
@RequestMapping("/clinic")
public class ClinicController {

    private final ClinicService clinicService;

    @Autowired
    public ClinicController(ClinicService clinicService) {
        this.clinicService = clinicService;
    }

    @PostMapping
    public ResponseEntity<String> addClinic(@RequestBody Clinic clinic) {
        return ResponseEntity.ok(clinicService.saveClinic(clinic));
    }

    @PutMapping
    public ResponseEntity<String> updateClinic(@RequestBody Clinic clinic) {
        return ResponseEntity.ok(clinicService.updateClinic(clinic));
    }

    @DeleteMapping("/{clinicId}")
    public ResponseEntity<String> deleteClinic(@PathVariable("clinicId") Long clinicId) {
        return ResponseEntity.ok(clinicService.deleteClinic(clinicId));
    }

    @GetMapping("/allclinics")
    public ResponseEntity<List<Clinic>> getAllClinics() {
        List<Clinic> allClinics = clinicService.getAllClinics();
        if (allClinics != null && !allClinics.isEmpty()) {
            return ResponseEntity.ok(allClinics);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<Clinic>> searchByName(@PathVariable("name") String name) {
        List<Clinic> allClinics = clinicService.searchByName(name);
        if (allClinics != null && !allClinics.isEmpty()) {
            return ResponseEntity.ok(allClinics);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{clinicId}")
    public ResponseEntity<Clinic> findById(@PathVariable("clinicId") Long clinicId) {
        return ResponseEntity.ok(clinicService.findClinicById(clinicId));
    }

    @GetMapping("/location/{address}")
    public ResponseEntity<List<Clinic>> findByAddress(@PathVariable("address") String address) {
        List<Clinic> allClinics = clinicService.findByAddressContaining(address);
        if (allClinics != null && !allClinics.isEmpty()) {
            return ResponseEntity.ok(allClinics);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
