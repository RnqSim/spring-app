package com.dcc.clinics.service;

import org.springframework.stereotype.Service;

import com.dcc.clinics.model.UnverifiedUser;
import com.dcc.clinics.model.User;
import com.dcc.clinics.model.Doctor;
import com.dcc.clinics.model.UnverifiedDoctor;
import com.dcc.clinics.repository.UnverifiedUserRepository;
import com.dcc.clinics.repository.UserRepository;
import com.dcc.clinics.repository.DoctorRepository;
import com.dcc.clinics.repository.UnverifiedDoctorRepository;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Service
public class DoctorService {
	private final UnverifiedUserRepository unverifiedUserRepository;
	private final UnverifiedDoctorRepository unverifiedDoctorRepository;
	private final UserRepository userRepository;
	private final DoctorRepository doctorRepository;
	private final JavaMailSender javaMailSender;
	private User loggedInDoctor;
	private User loggedInAdmin;

    @Autowired
    public DoctorService(UnverifiedUserRepository unverifiedUserRepository,UnverifiedDoctorRepository unverifiedDoctorRepository, UserRepository userRepository, DoctorRepository doctorRepository, JavaMailSender javaMailSender) {
        this.unverifiedUserRepository = unverifiedUserRepository;
		this.unverifiedDoctorRepository = unverifiedDoctorRepository;
		this.userRepository = userRepository;
		this.doctorRepository = doctorRepository;
		this.javaMailSender = javaMailSender;
    }
    
    public void sendVerification(String email, Integer code) {
        String subject = "Account Verification";
        sendVerificationEmail(email, subject, code);
	}
    
    
    public String addUser(User user, Doctor doctor) {
        User u = userRepository.findByUsername(user.getUsername());
        User uemail = userRepository.findByEmail(user.getEmail());
        UnverifiedUser unv = unverifiedUserRepository.findByUsername(user.getUsername());
        UnverifiedUser unvemail = unverifiedUserRepository.findByEmail(user.getEmail());
        
        if (u != null || unv != null) {
            return "Username is already in use.";
        } else {
            if (uemail != null || unvemail != null) {
                return "Email is already in use.";
            } else {
                BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
                String encryptedPassword = bcrypt.encode(user.getPassword());
                user.setPassword(encryptedPassword);
                int otp = generateVerificationCode();

                sendVerification(user.getEmail(), otp);

                UnverifiedUser newUser = new UnverifiedUser(user, otp);
                
                UnverifiedDoctor newDoctor = new UnverifiedDoctor(doctor);
                
                newDoctor.setApprovalStatus("Subject for Approval");

                // Save the unverified user and patient to their respective repositories
                unverifiedUserRepository.save(newUser);
                newDoctor.setUserId(newUser.getUserId());
                unverifiedDoctorRepository.save(newDoctor);
                

                return "User registered successfully";
            }
        }
    }
    
    public String addAdmin(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return "Username already exists. Please choose another one.";
        }
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        String encryptedPassword = bcrypt.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        user.setUserType("admin");
        userRepository.save(user);

        return "Admin registered successfully";
        
    }
    
    public Long getDoctorUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user.getUserId();
        } else {
            return null; // Return null or handle the case when the user is not found
        }
    }

    
    public String loginAdmin(String username, String password) {
        User storedUser = userRepository.findByUsername(username);

        if (storedUser == null) {
            return "Username not found.";
        }

        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        if (bcrypt.matches(password, storedUser.getPassword()) && "admin".equals(storedUser.getUserType())) {
        	setLoggedInAdmin(storedUser);
            return "Login successful";
        } else {
            return "Invalid username or password for admin login.";
        }
    }




    
    public boolean verifyUser(String email, Integer otp) {
        UnverifiedUser unvUser = unverifiedUserRepository.findByEmailAndOtp(email, otp);

        if (unvUser != null) {
            Long userId = unvUser.getUserId();
            UnverifiedDoctor unvDoctor = unverifiedDoctorRepository.findByUserId(userId);

            User user = new User(unvUser);
            user.setAvatar("Sample Icon");
            userRepository.save(user);
            
            Doctor doctor = new Doctor(unvDoctor);
            doctor.setApprovalStatus("Email Verified");
            doctorRepository.save(doctor);

            unverifiedDoctorRepository.deleteById(userId);
            unverifiedUserRepository.deleteById(userId);

            return true;
        } else {
            return false;
        }
    }
    
    public boolean login(String username, String password){
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        User user = userRepository.findByUsername(username);
           
        if (user != null && bcrypt.matches(password, user.getPassword())) {
            if ("doctor".equalsIgnoreCase(user.getUserType())) {
                // This is a doctor account, allow login

                // Get the associated doctor
                Doctor doctor = doctorRepository.findByUserId(user.getUserId());

                if (doctor != null && "Verified by Admin".equalsIgnoreCase(doctor.getApprovalStatus())) {
                    setLoggedInDoctor(user);
                    return true;
                } else if (doctor != null) {
                    // This is a doctor account, but it's not approved by the admin
                	   System.out.println("Doctor account is not approved by the admin.");
                } else {
                    // Handle the case where the doctor is not found
                	   System.out.println("Doctor associated with the user not found.");
                }
            } else {
                // This is not a doctor account, deny login
            	   System.out.println("Login denied: Not a doctor account.");
            }
        }
        
        // If the user is not found or the password doesn't match, deny login
        System.out.println("Login denied: Invalid credentials.");
		return false;
    }

    public String changePassword(String username, String oldPassword, String newPassword) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        User user = userRepository.findByUsername(username);

        String loggedInDoctorUsername = loggedInDoctor.getUsername();

        if(loggedInDoctorUsername != username && user != null && bcrypt.matches(oldPassword, user.getPassword())) {
            String encryptedPassword = bcrypt.encode(newPassword);
            user.setPassword(encryptedPassword);
            userRepository.save(user);
            return "Successfully changed password";
        }
        return "Failed to change password";
    }


    public void logout() {
        setLoggedInDoctor(null);
    }
    
    public void adminlogout() {
        setLoggedInAdmin(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Doctor getLoggedInDoctor() {
        return doctorRepository.findByUserId(loggedInDoctor.getUserId());
    }

    public void setLoggedInDoctor(User loggedInDoctor) {
        this.loggedInDoctor = loggedInDoctor;
    }
    
    public User getLoggedInAdmin() {
        return userRepository.findByUserId(loggedInAdmin.getUserId());
    }

    public void setLoggedInAdmin(User loggedInAdmin) {
        this.loggedInAdmin = loggedInAdmin;
    }
    public Long getLoggedInDoctorUserId() {
        if (loggedInDoctor != null) {
            return loggedInDoctor.getUserId();
        } else {
            // Return a default value or handle the case when no patient is logged in.
            return null; // You can choose an appropriate value or handle the case accordingly.
        }
    }

    public String setApprovalStatusForDoctor(Long userId, String approvalStatus) {
        if (loggedInAdmin == null) {
            return "Admin not logged in. Cannot set approval status.";
        }

        Doctor doctor = doctorRepository.findByUserId(userId);

        if (doctor != null) {
            doctor.setApprovalStatus(approvalStatus);
            doctorRepository.save(doctor);
            return "Doctor's approval status set to " + approvalStatus;
        } else {
            // Handle the case where the doctor is not found
            // You can throw an exception or handle it as needed
            return "Doctor not found for userId: " + userId;
        }
    }


    
	private void sendVerificationEmail(String to, String subject, Integer code) {
        SimpleMailMessage message = new SimpleMailMessage();
        String link = "http://localhost:8080/verify?email=" + to + "&otp=" + code;
        message.setTo(to);
        message.setSubject(subject);
        message.setText("To verify your account, please enter this code: " + String.valueOf(code) + "\n\nOr follow this link in your browser: " + link);
        javaMailSender.send(message);
    }
    private Integer generateVerificationCode() {
    	Random random = new Random();
        int min = 100000;
        int max = 999999;
        int randomNumber = random.nextInt(max - min + 1) + min;
        return randomNumber;
    }
    
    public List<Doctor> getAllUsers() {
        // Retrieve all registered users from the UserRepository
        return doctorRepository.findAll();
    }
    
    public String updateUserAndDoctorDetails(User user, Doctor doctor) {
        if (loggedInDoctor == null) {
            return "User not logged in"; // User is not logged in
        }

        Long userId = loggedInDoctor.getUserId(); // Get the userId from the logged-in user
        User existingUser = userRepository.findByUserId(userId);
        Doctor existingDoctor = doctorRepository.findByUserId(userId);

        if (existingUser != null && existingDoctor != null) {
            // Update user details
            existingUser.setFirstName(user.getFirstName());
            existingUser.setMiddleName(user.getMiddleName());
            existingUser.setLastName(user.getLastName());
            existingUser.setAge(user.getAge());
            existingUser.setSex(user.getSex());
            existingUser.setBirthday(user.getBirthday());
            existingUser.setAddress(user.getAddress());
            existingUser.setContactNumber(user.getContactNumber());
            existingUser.setEmail(user.getEmail());
            existingUser.setAvatar(user.getAvatar());

            // Update patient details
            existingDoctor.setPrcId(doctor.getPrcId());
            existingDoctor.setSpecialization(doctor.getSpecialization());
            existingDoctor.setCredentials(doctor.getCredentials());
            existingDoctor.setSecretary(doctor.getSecretary());
            existingDoctor.setLicenseNumber(doctor.getLicenseNumber());
            existingDoctor.setPtrNumber(doctor.getPtrNumber());

            // Save updated user and patient details
            userRepository.save(existingUser);
            doctorRepository.save(existingDoctor);

            return "User and doctor details updated successfully";
        } else {
            return "User or doctor not found for the given ID";
        }
    }
}