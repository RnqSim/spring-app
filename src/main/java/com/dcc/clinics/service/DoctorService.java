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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

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
	private List<User> loggedInDoctors = new ArrayList<>();
	private List<User> loggedInAdmins = new ArrayList<>();



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
    

    public Doctor getDoctorProfile(Long doctorUserId) {
    	return doctorRepository.findByUserId(doctorUserId);
    }
    
    public User getAdminProfile(Long adminId) {
    	return userRepository.findByUserId(adminId);
    }
    
    
    public Doctor getDoctorByUsername(String username) {
        User user = userRepository.findByUsername(username);

        for (User loggedInDoctors : loggedInDoctors) {
            if (loggedInDoctors.getUserId().equals(user.getUserId())) {
                return getDoctorProfile(user.getUserId());
            }
        }

        return null;
    }
    
    public User getAdminByUsername(String username) {
        User user = userRepository.findByUsername(username);

        for (User loggedInAdmins : loggedInAdmins) {
            if (loggedInAdmins.getUserId().equals(user.getUserId())) {
                return getAdminProfile(user.getUserId());
            }
        }

        return null;
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
            loggedInAdmins.add(storedUser);
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
                    loggedInDoctors.add(user);
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

        if( user != null && bcrypt.matches(oldPassword, user.getPassword())) {
            String encryptedPassword = bcrypt.encode(newPassword);
            user.setPassword(encryptedPassword);
            userRepository.save(user);
            return "Successfully changed password";
        }
        return "Failed to change password";
    }
    



    public void logout(String username) {
        // Remove the logged-out patient from the list based on the username
        loggedInDoctors.removeIf(user -> user.getUsername().equals(username));
    }
    
    public void adminlogout(String username) {
        // Remove the logged-out patient from the list based on the username
        loggedInAdmins.removeIf(user -> user.getUsername().equals(username));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public void setLoggedInDoctor(User loggedInDoctor) {
    	loggedInDoctors.add(loggedInDoctor);    
    }
    
    public List<Long> getLoggedInDoctorUserIds() {
        // Return a list of user IDs for all logged-in patients
        return loggedInDoctors.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());
    }



    public void setLoggedInAdmin(User loggedInAdmin) {
    	loggedInDoctors.add(loggedInAdmin);
    }
    

    public String setApprovalStatusForDoctor(Long userId, String approvalStatus) {

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
    
    public String updateUserAndDoctorDetails(String username, User user, Doctor doctor) {

        if (loggedInDoctors == null) {
            return "User not logged in";
        }

        // Find the logged-in user based on the username
        Optional<User> optionalUser = loggedInDoctors.stream()
                .filter(loggedInUser -> loggedInUser.getUsername().equals(username))
                .findFirst();

        // Check if the user is found
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            Long userId = existingUser.getUserId(); // Get the userId from the logged-in user

            // Find existing user and doctor by userId
            User existingUserFromDb = userRepository.findByUserId(userId);
            Doctor existingDoctorFromDb = doctorRepository.findByUserId(userId);

            if (existingUserFromDb != null && existingDoctorFromDb != null) {
                // Update user details
                existingUserFromDb.setFirstName(user.getFirstName());
                existingUserFromDb.setMiddleName(user.getMiddleName());
                existingUserFromDb.setLastName(user.getLastName());
                existingUserFromDb.setAge(user.getAge());
                existingUserFromDb.setSex(user.getSex());
                existingUserFromDb.setBirthday(user.getBirthday());
                existingUserFromDb.setAddress(user.getAddress());
                existingUserFromDb.setContactNumber(user.getContactNumber());
                existingUserFromDb.setEmail(user.getEmail());
                existingUserFromDb.setAvatar(user.getAvatar());

                // Update doctor details
                existingDoctorFromDb.setPrcId(doctor.getPrcId());
                existingDoctorFromDb.setSpecialization(doctor.getSpecialization());
                existingDoctorFromDb.setCredentials(doctor.getCredentials());
                existingDoctorFromDb.setSecretary(doctor.getSecretary());
                existingDoctorFromDb.setLicenseNumber(doctor.getLicenseNumber());
                existingDoctorFromDb.setPtrNumber(doctor.getPtrNumber());

                // Save updated user and doctor details
                userRepository.save(existingUserFromDb);
                doctorRepository.save(existingDoctorFromDb);

                return "User and doctor details updated successfully";
            } else {
                return "User or doctor not found for the given ID";
            }
        } else {
            return "User not found";
        }
    }
}