package com.dcc.clinics.controller;

import com.dcc.clinics.model.Patient;
import com.dcc.clinics.model.User;
import com.dcc.clinics.model.UserPatientRequest;
import com.dcc.clinics.service.PatientService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "https://spring-render-qpn7.onrender.com")
public class PatientController {
    private final PatientService patientService;
    private static boolean isPatientLoggedIn = false;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping("/patients")
    public ResponseEntity<String> addUser(@RequestBody UserPatientRequest userPatientRequest) {
        User user = userPatientRequest.getUser();
        Patient patient = userPatientRequest.getPatient();
        return ResponseEntity.ok(patientService.addUser(user, patient));
    }
    
    @GetMapping("/checkLoggedInPatient")
    public Long getLoggedInPatientUserId() {
        return patientService.getLoggedInPatientUserId();
    }
    
    @GetMapping("/patientview/{patientUserId}")
    public Patient getPatientProfile(@PathVariable Long patientUserId) {
        return patientService.getPatientProfile(patientUserId);
    }

	@CrossOrigin(origins = "http://localhost:3000")
    @PutMapping("/editpatient")
    public ResponseEntity<String> updateDetails(@RequestBody UserPatientRequest userPatientRequest) {
    	User user = userPatientRequest.getUser();
        Patient patient = userPatientRequest.getPatient();
        String result = patientService.updateUserAndPatientDetails(user, patient);
        if ("User and patient details updated successfully".equals(result)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }


	@CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("/patientverify")
    public ResponseEntity<String> verifyUser(@RequestParam("email") String email,
            								@RequestParam("otp") Integer otp) {
    	if (patientService.verifyUser(email, otp)) {
    		return ResponseEntity.ok("Successful verification.");
    	} else {
    		return ResponseEntity.ok("Unsuccessful verification.");
    	}
    }

    @PostMapping("/patientlogin")
    public ResponseEntity<String> login(@RequestParam("username") String username,
                                        @RequestParam("password") String password) {
        if (isPatientLoggedIn) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Another user is already logged in");
        }

        if (patientService.login(username, password)) {
            isPatientLoggedIn = true;
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }

    @PostMapping("/patientlogout")
    public ResponseEntity<String> logout() {
        if (!isPatientLoggedIn) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user is currently logged in");
        }

        patientService.logout();
        isPatientLoggedIn = false;
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/changePass/patient")
    public ResponseEntity<String> changePassword(
            @RequestParam ("username") String username,
            @RequestParam ("oldPassword") String oldPassword,
            @RequestParam ("newPassword") String newPassword) {
        if (!isPatientLoggedIn) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user is currently logged in");
        }
        return ResponseEntity.ok(patientService.changePassword(username, oldPassword, newPassword));
    }
    
    @GetMapping("/patientprofile")
    public ResponseEntity<Patient> getUserProfile() {
        Patient loggedInUser = patientService.getLoggedInUser();
        if (loggedInUser != null) {
            return ResponseEntity.ok(loggedInUser);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/allpatients")
    public ResponseEntity<List<Patient>> viewAllUsers() {
        List<Patient> allUsers = patientService.getAllUsers();
        if (allUsers != null && !allUsers.isEmpty()) {
            return ResponseEntity.ok(allUsers);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}