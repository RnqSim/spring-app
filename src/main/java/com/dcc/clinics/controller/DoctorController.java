package com.dcc.clinics.controller;

import com.dcc.clinics.model.Doctor;
import com.dcc.clinics.model.User;
import com.dcc.clinics.model.UserDoctorRequest;
import com.dcc.clinics.service.DoctorService;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "https://docclickconnect.vercel.app")
public class DoctorController {
    private final DoctorService doctorService;

    @Autowired
    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping("/doctors")
    public ResponseEntity<String> addUser(@RequestBody UserDoctorRequest userDoctorRequest) {
    	 User user = userDoctorRequest.getUser();
         Doctor doctor = userDoctorRequest.getDoctor();
    	return ResponseEntity.ok(doctorService.addUser(user, doctor));
    }
    
    
    @PostMapping("/doctorverify")
    public ResponseEntity<String> verifyUser(@RequestParam("email") String email,
            								@RequestParam("otp") Integer otp) {
    	if (doctorService.verifyUser(email, otp)) {
    		return ResponseEntity.ok("Successful verification.");
    	} else {
    		return ResponseEntity.ok("Unsuccessful verification.");
    	}
    }

	 @GetMapping("/getDoctorUserId")
	    public ResponseEntity<Long> getDoctorUserId(@RequestParam String username) {
	        Long doctorUserId = doctorService.getDoctorUserIdByUsername(username);
	        if (doctorUserId != null) {
	            return ResponseEntity.ok(doctorUserId);
	        } else {
	            // Handle the case when the doctor's user is not found
	            return ResponseEntity.notFound().build();
	        }
	    }

    
    @GetMapping("/doctordetails/{username}")
    public ResponseEntity<Doctor> getDoctorByUsername(@PathVariable String username) {
    	Doctor user = doctorService.getDoctorByUsername(username);

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/admindetails/{username}")
    public ResponseEntity<User> getAdminByUsername(@PathVariable String username) {
        User admin = doctorService.getAdminByUsername(username);

        if (admin != null) {
            return new ResponseEntity<>(admin, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    @GetMapping("/allusers")
    public ResponseEntity<List<Doctor>> viewAllUsers() {
        List<Doctor> allUsers = doctorService.getAllUsers();
        if (allUsers != null && !allUsers.isEmpty()) {
            return ResponseEntity.ok(allUsers);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
	
	@PostMapping("/approval")
    public String setApprovalStatusForDoctor(
    		@RequestParam Long userId,
            @RequestParam String approvalStatus
    ) {
        String result = doctorService.setApprovalStatusForDoctor(userId, approvalStatus);
        return result;
    }
	
	@CrossOrigin(origins = "http://localhost:3000")

    @PutMapping("/editdoctor/{username}")
    public ResponseEntity<String> updateDetails(@RequestBody UserDoctorRequest userDoctorRequest, @PathVariable String username) {
    	User user = userDoctorRequest.getUser();
        Doctor doctor = userDoctorRequest.getDoctor();
        String result = doctorService.updateUserAndDoctorDetails(username, user, doctor);
        if ("User and doctor details updated successfully".equals(result)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/doctorlogin")
    public ResponseEntity<String> login(@RequestParam("username") String username,
                                        @RequestParam("password") String password) {

        if (doctorService.login(username, password)) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }

    @PostMapping("/doctorlogout/{username}")
    public ResponseEntity<String> logout(@PathVariable String username) {

        doctorService.logout(username);
        return ResponseEntity.ok("Logged out successfully");
    }
    
    @PostMapping("/adminlogout/{username}")
    public ResponseEntity<String> logoutAdmin(@PathVariable String username) {
        doctorService.adminlogout(username);
        return ResponseEntity.ok("Logged out successfully");
    }
  
    @PostMapping("/changePass/doctor")
    public ResponseEntity<String> changePassword(
            @RequestParam ("username") String username,
            @RequestParam ("oldPassword") String oldPassword,
            @RequestParam ("newPassword") String newPassword) {

        return ResponseEntity.ok(doctorService.changePassword(username, oldPassword, newPassword));
    }
    

    @PostMapping("/adminCreate")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        String registrationResult = doctorService.addAdmin(user);
        if ("User registered successfully".equals(registrationResult)) {
            return ResponseEntity.ok(registrationResult);
        } else {
            return ResponseEntity.badRequest().body(registrationResult);
        }
    }
    
    @PostMapping("/adminlogin")
    public ResponseEntity<String> adminLogin(@RequestParam("username") String username,
                                             @RequestParam("password") String password) {


        String loginResult = doctorService.loginAdmin(username, password);

        if ("Login successful".equals(loginResult)) {
            return ResponseEntity.ok("Admin login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin login failed: " + loginResult);
        }
    }
}