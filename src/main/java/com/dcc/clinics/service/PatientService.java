package com.dcc.clinics.service;

import org.springframework.stereotype.Service;

import com.dcc.clinics.model.UnverifiedPatient;
import com.dcc.clinics.model.UnverifiedUser;
import com.dcc.clinics.model.User;
import com.dcc.clinics.model.Patient;
import com.dcc.clinics.repository.UnverifiedPatientRepository;
import com.dcc.clinics.repository.UnverifiedUserRepository;
import com.dcc.clinics.repository.UserRepository;
import com.dcc.clinics.repository.PatientRepository;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Service
public class PatientService {
	private final UnverifiedUserRepository unverifiedUserRepository;
	private final UnverifiedPatientRepository unverifiedPatientRepository;
	private final UserRepository userRepository;
	private final PatientRepository patientRepository;
	private final JavaMailSender javaMailSender;
	private User loggedInPatient;

    @Autowired
    public PatientService(UnverifiedUserRepository unverifiedUserRepository,UnverifiedPatientRepository unverifiedPatientRepository, UserRepository userRepository, PatientRepository patientRepository, JavaMailSender javaMailSender) {
        this.unverifiedUserRepository = unverifiedUserRepository;
		this.unverifiedPatientRepository = unverifiedPatientRepository;
		this.userRepository = userRepository;
		this.patientRepository = patientRepository;
		this.javaMailSender = javaMailSender;
    }
    
    public void sendVerification(String email, Integer code) {
        String subject = "Account Verification";
        sendVerificationEmail(email, subject, code);
	}
    
    
    public String addUser(User user, Patient patient) {
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
                UnverifiedPatient newPatient = new UnverifiedPatient(patient);
                
                // Save the unverified user and patient to their respective repositories
                unverifiedUserRepository.save(newUser);
                newPatient.setUserId(newUser.getUserId());
                unverifiedPatientRepository.save(newPatient);

                return "User registered successfully";
            }
        }
    }

    
    public boolean verifyUser(String email, Integer otp) {
    	UnverifiedUser unvUser = unverifiedUserRepository.findByEmailAndOtp(email, otp);
    	if (unvUser != null) {
    		Long userId = unvUser.getUserId();
    		UnverifiedPatient unvPatient = unverifiedPatientRepository.findByUserId(userId);
    		User user = new User(unvUser);
    	    user.setAvatar("Sample Icon");
    		userRepository.save(user);
    		
    		Patient patient = new Patient(unvPatient);
    		patientRepository.save(patient);
    		
    		unverifiedPatientRepository.deleteById(userId);
    		unverifiedUserRepository.deleteById(userId);
    		return true;
    	} else {
    		return false;
    	}
	}

    
    public boolean login(String username, String password) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        User user = userRepository.findByUsername(username);
        
        if (user != null && bcrypt.matches(password, user.getPassword())) {
            if ("patient".equalsIgnoreCase(user.getUserType())) {
                // This is a patient account, allow login
                setLoggedInPatient(user);
                return true;
            } else {
                // This is not a patient account, deny login and print an error message
                System.out.println("Login denied: Not a patient account.");
                return false;
            }
        }
        
        // If the user is not found or the password doesn't match, deny login
        System.out.println("Login denied: Invalid credentials.");
        return false;
    }

    public String changePassword(String username, String oldPassword, String newPassword) {
        BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
        User user = userRepository.findByUsername(username);

        String loggedInPatientUsername = loggedInPatient.getUsername();

        if(loggedInPatientUsername != username && user != null && bcrypt.matches(oldPassword, user.getPassword())) {
            String encryptedPassword = bcrypt.encode(newPassword);
            user.setPassword(encryptedPassword);
            userRepository.save(user);
            return "Successfully changed password";
        }
        return "Failed to change password";
    }


    public void logout() {
        setLoggedInPatient(null);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Patient getLoggedInUser() {
        return patientRepository.findByUserId(loggedInPatient.getUserId());
    }
    
    public Patient getPatientProfile(Long patientUserId) {
    	return patientRepository.findByUserId(patientUserId);
    }

    public void setLoggedInPatient(User loggedInPatient) {
        this.loggedInPatient = loggedInPatient;
    }
    
    public Long getLoggedInPatientUserId() {
        if (loggedInPatient != null) {
            return loggedInPatient.getUserId();
        } else {
            // Return a default value or handle the case when no patient is logged in.
            return null; // You can choose an appropriate value or handle the case accordingly.
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
    
    public List<Patient> getAllUsers() {
        // Retrieve all registered users from the UserRepository
        return patientRepository.findAll();
    }
    public String updateUserAndPatientDetails(User user, Patient patient) {
        if (loggedInPatient == null) {
            return "User not logged in"; // User is not logged in
        }

        Long userId = loggedInPatient.getUserId(); // Get the userId from the logged-in user
        User existingUser = userRepository.findByUserId(userId);
        Patient existingPatient = patientRepository.findByUserId(userId);

        if (existingUser != null && existingPatient != null) {
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
            existingPatient.setSeniorId(patient.getSeniorId());
            existingPatient.setPwdId(patient.getPwdId());
            existingPatient.setPhilhealthId(patient.getPhilhealthId());
            existingPatient.setHmo(patient.getHmo());

            // Save updated user and patient details
            userRepository.save(existingUser);
            patientRepository.save(existingPatient);

            return "User and patient details updated successfully";
        } else {
            return "User or patient not found for the given ID";
        }
    }

}