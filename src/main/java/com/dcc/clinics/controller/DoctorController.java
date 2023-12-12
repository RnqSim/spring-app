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
@CrossOrigin(origins = "http://localhost:3000")
public class DoctorController {
    private final DoctorService doctorService;
    private static boolean isDoctorLoggedIn = false;
    private static boolean isAdminLoggedIn = false;

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
    
    @GetMapping("/checkLoggedInDoctor")
    public Long getLoggedInDoctorUserId() {
        return doctorService.getLoggedInDoctorUserId();
    }
    
    @GetMapping("/checkLoggedInAdmin")
    public User getLoggedInAdmin() {
        return doctorService.getLoggedInAdmin();
    }
    
	@CrossOrigin(origins = "https://docclickconnect.vercel.app")
    @GetMapping("/doctorverify")
    public ResponseEntity<String> verifyUser(@RequestParam("email") String email,
            								@RequestParam("otp") Integer otp) {
    	if (doctorService.verifyUser(email, otp)) {
    		return ResponseEntity.ok("Successful verification.");
    	} else {
    		return ResponseEntity.ok("Unsuccessful verification.");
    	}
    }
	@CrossOrigin(origins = "https://docclickconnect.vercel.app")
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
    @GetMapping("/doctorprofile")
    public ResponseEntity<Doctor> getUserProfile() {
        Doctor loggedInUser = doctorService.getLoggedInDoctor();
        if (loggedInUser != null) {
            return ResponseEntity.ok(loggedInUser);
        } else {
            return ResponseEntity.notFound().build();
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
	
	@CrossOrigin(origins = "https://docclickconnect.vercel.app")

    @PutMapping("/editdoctor")
    public ResponseEntity<String> updateDetails(@RequestBody UserDoctorRequest userDoctorRequest) {
    	User user = userDoctorRequest.getUser();
        Doctor doctor = userDoctorRequest.getDoctor();
        String result = doctorService.updateUserAndDoctorDetails(user, doctor);
        if ("User and doctor details updated successfully".equals(result)) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/doctorlogin")
    public ResponseEntity<String> login(@RequestParam("username") String username,
                                        @RequestParam("password") String password) {
        if (isDoctorLoggedIn) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Another user is already logged in");
        }

        if (doctorService.login(username, password)) {
            isDoctorLoggedIn = true;
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
        }
    }

    @PostMapping("/doctorlogout")
    public ResponseEntity<String> logout() {
        if (!isDoctorLoggedIn) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user is currently logged in");
        }

        doctorService.logout();
        isDoctorLoggedIn = false;
        return ResponseEntity.ok("Logged out successfully");
    }
    
    @PostMapping("/adminlogout")
    public ResponseEntity<String> logoutAdmin() {
        if (!isAdminLoggedIn) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No admin is currently logged in");
        }

        doctorService.adminlogout();
        isAdminLoggedIn = false;
        return ResponseEntity.ok("Logged out successfully");
    }
  
    @PostMapping("/changePass/doctor")
    public ResponseEntity<String> changePassword(
            @RequestParam ("username") String username,
            @RequestParam ("oldPassword") String oldPassword,
            @RequestParam ("newPassword") String newPassword) {
        if (!isDoctorLoggedIn) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No user is currently logged in");
        }
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
        if (isAdminLoggedIn) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Another admin is already logged in");
        }

        String loginResult = doctorService.loginAdmin(username, password);

        if ("Login successful".equals(loginResult)) {
            isAdminLoggedIn = true;
            return ResponseEntity.ok("Admin login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Admin login failed: " + loginResult);
        }
    }
}