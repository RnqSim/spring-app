package com.dcc.clinics.service;

import com.dcc.clinics.model.Clinic;
import com.dcc.clinics.model.Schedule;
import com.dcc.clinics.model.User;
import com.dcc.clinics.repository.ClinicRepository;
import com.dcc.clinics.repository.ScheduleRepository;
import com.dcc.clinics.repository.UserRepository;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import com.dcc.clinics.model.Appointment;
import com.dcc.clinics.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;
	private final JavaMailSender javaMailSender;
	
	private static final String APPLICATION_NAME = "Doc Click Connect";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final Set<String> SCOPES = Collections.singleton(CalendarScopes.CALENDAR);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, ScheduleRepository scheduleRepository, ClinicRepository clinicRepository, UserRepository userRepository, JavaMailSender javaMailSender) {
        this.appointmentRepository = appointmentRepository;
        this.scheduleRepository = scheduleRepository;
        this.clinicRepository = clinicRepository;
        this.userRepository = userRepository;
		this.javaMailSender = javaMailSender;
    }

    private static GoogleAuthorizationCodeFlow flow;
    private static String redirectUri;
    private static String userId;
    private static Credential credential;

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = AppointmentService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .setApprovalPrompt("auto")
                .build();

        userId = "user";
        LocalServerReceiverHttps receiver = new LocalServerReceiverHttps.Builder().setHost("spring-render-qpn7.onrender.com").build();
//        credential = flow.loadCredential("user");
//        if (credential != null
//                && (credential.getRefreshToken() != null
//                || credential.getExpiresInSeconds() == null
//                || credential.getExpiresInSeconds() > 60)) {
//            return credential;
//        }
//
//        // open in browser
//        redirectUri = receiver.getRedirectUri();
//        AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectUri);
//        String url = authorizationUrl.build();
//        Preconditions.checkNotNull(url);
//        // redirect somehow

//        LocalServerReceiverHttps receiver = new LocalServerReceiverHttps.Builder().setHost("spring-render-qpn7.onrender.com").build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        return credential;
        //Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        //String url = flow.newAuthorizationUrl().setRedirectUri("https://spring-render-qpn7.onrender.com/Callback").build();
        //String code ;
        //GoogleTokenResponse response = flow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute();

        //GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
    }

    public String saveAppointment(Long patientId, Long scheduleId, Date scheduleDate, String status) {
        // restrict a Patient from scheduling the same time (can be different doctors)
        List<Appointment> existingAppointmentsOfSamePatient = appointmentRepository.findAllByPatientUserId(patientId);
        Schedule schedule = scheduleRepository.findByScheduleId(scheduleId);

        for(Appointment a : existingAppointmentsOfSamePatient) {
            Schedule oldSchedule = scheduleRepository.findByScheduleId(a.getScheduleId());
            if (scheduleDate.compareTo(a.getScheduleDate()) == 0 &&
                    oldSchedule.getStartTime().compareTo(schedule.getEndTime()) <= 0 &&
                    schedule.getStartTime().compareTo(oldSchedule.getEndTime()) <= 0
            ) {
                return "Patient has already scheduled for that same time slot";
            }
        }

        Clinic clinic = clinicRepository.findByClinicId(schedule.getClinicId());
        User doctor = userRepository.findByUserId(schedule.getDoctorUserId());
        User patient = userRepository.findByUserId(patientId);

        Appointment appointment = new Appointment();
        appointment.setPatientUserId(patientId);
        appointment.setDoctorUserId(schedule.getDoctorUserId());
        appointment.setClinicId(schedule.getClinicId());
        appointment.setClinicName(clinic.getName());
        appointment.setAddress(clinic.getAddress());

        String patientName = patient.getFirstName() + " " + patient.getMiddleName() + " " + patient.getLastName();
        String doctorName = doctor.getFirstName() + " " + doctor.getMiddleName() + " " + doctor.getLastName();
        appointment.setPatientName(patientName);
        appointment.setDoctorName(doctorName);

        appointment.setScheduleId(scheduleId);
        appointment.setScheduleDay(schedule.getScheduleDay());
        appointment.setScheduleDate(scheduleDate);
        appointment.setStartTime(schedule.getStartTime());
        appointment.setEndTime(schedule.getEndTime());
        appointment.setStatus(status);

        // set slots
        List<Appointment> sameScheduleAppointments = appointmentRepository.findAllByScheduleId(scheduleId);
        if (sameScheduleAppointments.isEmpty()) {
        	Integer availableSlots = schedule.getSlots();

            // Check for null before subtracting 1
            if (availableSlots != null) {
                appointment.setSlots(availableSlots - 1);
            }
        } else {
            for (Appointment toUpdateAppointment : sameScheduleAppointments) {
                if(toUpdateAppointment.getScheduleDate().compareTo(scheduleDate) == 0) {
                    if(status.compareTo("Cancelled") != 0){
                        // not cancelled, reduce slots
                        if(toUpdateAppointment.getSlots() == 0) {
                            return "Appointment failed to save. No more slots available";
                        }
                        toUpdateAppointment.setSlots(toUpdateAppointment.getSlots()-1);
                    } // cancelled do not reduce slots - cancelled upon creation is unrealistic but caught it just in case
                    appointment.setSlots(toUpdateAppointment.getSlots());
                    appointmentRepository.save(toUpdateAppointment);
                }
            }
        }

//        try {
//
//            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//            Credential credentials = getCredentials(HTTP_TRANSPORT);
//            Calendar calendarService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
//                    .setApplicationName(APPLICATION_NAME)
//                    .build();
//
//            LocalDate localDate = scheduleDate.toLocalDate();
//            LocalTime startLocalTime = schedule.getStartTime().toLocalTime().minusHours(8);
//            LocalDateTime startLocalDateTime = LocalDateTime.of(localDate, startLocalTime);
//            LocalTime endLocalTime = schedule.getEndTime().toLocalTime().minusHours(8);
//            LocalDateTime endLocalDateTime = LocalDateTime.of(localDate, endLocalTime);
//
//            Event event = new Event()
//                    .setSummary("Appointment at " + clinic.getName())
//                    .setLocation(clinic.getAddress());
//
//            if(status.compareTo("Scheduled by Patient")==0) {
//                event.setDescription("Status: Waiting for Doctor Confirmation");
//            } else if (status.compareTo("Confirmed by Doctor")==0) {
//                event.setDescription("Status: Confirmed");
//            } else if (status.compareTo("Cancelled")==0) {
//                event.setDescription("Status: Cancelled");
//            } else if (status.compareTo("Rescheduled")==0) {
//                event.setDescription("Rescheduled");
//            } else {
//                event.setDescription("Unknown status");
//            }
//
//            DateTime startDateTime = new DateTime(startLocalDateTime.toString()+":00");
//            EventDateTime start = new EventDateTime();
//            start.setDateTime(startDateTime);
//
//            DateTime endDateTime = new DateTime(endLocalDateTime.toString()+":00");
//            EventDateTime end = new EventDateTime();
//            end.setDateTime(endDateTime);
//
//            event.setStart(start);
//            event.setEnd(end);
//
//            EventAttendee[] attendees = new EventAttendee[] {
//                    new EventAttendee().setEmail(doctor.getEmail()),
//                    new EventAttendee().setEmail(patient.getEmail()),
//            };
//            event.setAttendees(Arrays.asList(attendees));
//
//            EventReminder[] reminderOverrides = new EventReminder[] {
//                    new EventReminder().setMethod("email").setMinutes(24 * 60),
//                    new EventReminder().setMethod("popup").setMinutes(10),
//            };
//            Event.Reminders reminders = new Event.Reminders()
//                    .setUseDefault(false)
//                    .setOverrides(Arrays.asList(reminderOverrides));
//            event.setReminders(reminders);
//
//            String calendarId = "primary";
//            event = calendarService.events().insert(calendarId, event)
//                    .setSendNotifications(true)
//                    .execute();
//            System.out.printf("Event created: %s\n", event.getHtmlLink());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Failed to Update Google Calendar";
//        }

        appointmentRepository.save(appointment);
        return "Appointment saved to database";
    }

    public String updateAppointment(Long appointmentId,  Date scheduleDate) {
        Optional<Appointment> existingAppointment = appointmentRepository.findById(appointmentId);

        if (existingAppointment.isPresent()) {
            Appointment appointment = existingAppointment.get();
            Date oldScheduleDate = appointment.getScheduleDate();
            LocalDateTime currentDateTime = LocalDateTime.now();

            List<Appointment> existingAppointmentsOfSamePatient = appointmentRepository.findAllByPatientUserId(appointment.getPatientUserId());
            // Will include same appointment
            Schedule schedule = scheduleRepository.findByScheduleId(appointment.getScheduleId());

            for(Appointment a : existingAppointmentsOfSamePatient) {
                Schedule oldSchedule = scheduleRepository.findByScheduleId(a.getScheduleId());
                if (scheduleDate.compareTo(a.getScheduleDate()) == 0 &&
                        oldSchedule.getStartTime().compareTo(schedule.getEndTime()) <= 0 &&
                        schedule.getStartTime().compareTo(oldSchedule.getEndTime()) <= 0
                ) {
                    return "Patient has already scheduled for that same time slot";
                }
            }

            // Calculate the time difference between scheduleDate and currentDateTime
            long timeDifference = oldScheduleDate.getTime() - Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant()).getTime();
            long hoursDifference = Math.abs(TimeUnit.MILLISECONDS.toHours(timeDifference));

            if (hoursDifference >= 24) {
                appointment.setScheduleDate(scheduleDate);
                updateAppointmentStatus(appointment.getTransactionNo(), "Rescheduled");
                appointmentRepository.save(appointment);
                return "Appointment updated successfully";
            } else {
                return "Cannot update appointment within 24 hours of the scheduled time.";
            }
        } else {
            return "Appointment not found";
        }
    }

    public String updateAppointmentStatus(Long appointmentId, String newStatus) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        
        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            String oldStatus = appointment.getStatus();

            appointment.setStatus(newStatus);
            Date scheduleDate = appointment.getScheduleDate();

//            try {
//                final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//                Credential credentials = getCredentials(HTTP_TRANSPORT);
//                Calendar calendarService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials)
//                        .setApplicationName(APPLICATION_NAME)
//                        .build();
//
//                String newDescription;
//                String oldDescription;
//
                Long scheduleId = appointment.getScheduleId();
                Schedule schedule = scheduleRepository.findByScheduleId(scheduleId);
//
//                LocalDate localDate = scheduleDate.toLocalDate();
//                LocalTime startLocalTime = schedule.getStartTime().toLocalTime().minusHours(8);
//                LocalDateTime startLocalDateTime = LocalDateTime.of(localDate, startLocalTime);
//                LocalTime endLocalTime = schedule.getEndTime().toLocalTime().minusHours(8);
//                LocalDateTime endLocalDateTime = LocalDateTime.of(localDate, endLocalTime);
//
//                DateTime startDateTime = new DateTime(startLocalDateTime.toString()+":00");
//                DateTime endDateTime = new DateTime(endLocalDateTime.toString()+":00");
//
//                if(oldStatus.compareTo("Scheduled by Patient")==0) {
//                    oldDescription = "Status: Waiting for Doctor Confirmation";
//                } else if (oldStatus.compareTo("Confirmed by Doctor")==0) {
//                    oldDescription = "Status: Confirmed";
//                } else if (oldStatus.compareTo("Cancelled")==0) {
//                    oldDescription = "Status: Cancelled";
//                } else if (oldStatus.compareTo("Rescheduled")==0) {
//                    oldDescription = "Rescheduled";
//                } else {
//                    oldDescription = "Unknown status";
//                }
//
//                if(newStatus.compareTo("Scheduled by Patient")==0) {
//                    newDescription = "Status: Waiting for Doctor Confirmation";
//                } else if (newStatus.compareTo("Confirmed by Doctor")==0) {
//                    newDescription = "Status: Confirmed";
//                } else if (newStatus.compareTo("Cancelled")==0) {
//                    newDescription = "Status: Cancelled";
//                } else if (newStatus.compareTo("Rescheduled")==0) {
//                    newDescription = "Rescheduled";
//                } else {
//                    newDescription = "Unknown status";
//                }

                List<Appointment> sameScheduleAppointments = appointmentRepository.findAllByScheduleId(scheduleId);
                for (Appointment toUpdateAppointment : sameScheduleAppointments) {
                    if(toUpdateAppointment.getScheduleDate().compareTo(scheduleDate) == 0) {
                        if(oldStatus.compareTo("Cancelled") == 0 && newStatus.compareTo("Cancelled") != 0){
                            // getting uncancelled, reduce slots
                            if(toUpdateAppointment.getSlots() == 0) {
                                return "Appointment failed to save. No more slots available";
                            }
                            toUpdateAppointment.setSlots(toUpdateAppointment.getSlots()-1);
                        } else if (oldStatus.compareTo("Cancelled") != 0 && newStatus.compareTo("Cancelled") == 0) {
                            // getting cancelled, increase slots
                            toUpdateAppointment.setSlots(toUpdateAppointment.getSlots()+1);
                        }
                        appointmentRepository.save(toUpdateAppointment);
                    }
                }
//
//                System.out.println("Old: " + oldDescription);
//                System.out.println("New: " + newDescription);
//
//                String pageToken = null;
//                do {
//                    Events events = calendarService.events().list("primary").setPageToken(pageToken).execute();
//                    List<Event> items = events.getItems();
//                    for (Event event : items) {
//                        System.out.println(event.getSummary());
//                        if (event.getDescription().compareTo(oldDescription) == 0 &&
//                                event.getEnd().getDateTime().toString().compareTo(endDateTime.toString()) == 0 &&
//                                event.getStart().getDateTime().toString().compareTo(startDateTime.toString()) == 0) {
//                            System.out.println("Event Found ================================================================");
//                            event.setDescription(newDescription);
//                            Event updatedEvent = calendarService.events().update("primary", event.getId(), event).execute();
//                            break;
//                        }
//                    }
//                    pageToken = events.getNextPageToken();
//                } while (pageToken != null);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return "Failed to Update Google Calendar";
//            }
//            System.out.print("Update ================================================================");

            appointmentRepository.save(appointment);
            Long patientId = appointment.getPatientUserId();
            User patient = userRepository.findByUserId(patientId);
            String email = patient.getEmail();
            sendNotification(email, scheduleDate, newStatus);
            return "Appointment status updated successfully";
        } else {
            return "Appointment not found with the given ID";
        }
    }
    
    public String cancelAppointment(Long appointmentId) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);

        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();
            Date scheduleDate = appointment.getScheduleDate();
            LocalDateTime currentDateTime = LocalDateTime.now();

            // Calculate the time difference between scheduleDate and currentDateTime
            long timeDifference = scheduleDate.getTime() - Date.from(currentDateTime.atZone(ZoneId.systemDefault()).toInstant()).getTime();
            long hoursDifference = Math.abs(TimeUnit.MILLISECONDS.toHours(timeDifference));

            if (hoursDifference >= 24) {
                updateAppointmentStatus(appointmentId, "Cancelled");
                appointmentRepository.save(appointment);
                return "Appointment status updated to cancelled";
            } else {
                return "Cannot cancel appointment within 24 hours of the scheduled time.";
            }
        } else {
            return "Appointment not found";
        }
    }
    
    
    public String deleteAppointment(Long appointmentId) {
        Optional<Appointment> existingAppointment = appointmentRepository.findById(appointmentId);
        if (existingAppointment.isPresent()) {
            Appointment appointment = existingAppointment.get();
            Long scheduleId = appointment.getScheduleId();

            List<Appointment> sameScheduleAppointments = appointmentRepository.findAllByScheduleId(scheduleId);
            for (Appointment toUpdateAppointment : sameScheduleAppointments) {
                if(toUpdateAppointment.getScheduleDate().compareTo(appointment.getScheduleDate()) == 0) {
                    if(appointment.getStatus().compareTo("Cancelled") != 0){
                        // not cancelled, increase slots
                        toUpdateAppointment.setSlots(toUpdateAppointment.getSlots()+1);
                    } // if cancelled, slot adjustment already performed, no slot changes
                    appointmentRepository.save(toUpdateAppointment);
                }
            }
            appointmentRepository.deleteById(appointmentId);
            return "Appointment deleted successfully";
        }
        return "Appointment not found";
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    
    public ResponseEntity<String> checkSlotsAvailability(Long scheduleId, Date scheduleDate) {
        List<Appointment> appointmentsForSchedule = appointmentRepository.findAllByScheduleId(scheduleId);

        if (appointmentsForSchedule.isEmpty()) {
        	 Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);

             if (schedule != null) {
                 int remainingSlots = schedule.getSlots();
                 return ResponseEntity.ok(remainingSlots + " slots remaining.");
             }
            
        } else {
        	boolean isAppointmentOnDate = appointmentsForSchedule.stream()
        	        .anyMatch(appointment -> appointment.getScheduleDate().equals(scheduleDate));

        	if (isAppointmentOnDate) {
        	    Optional<Appointment> anyAppointment = appointmentsForSchedule.stream()
        	            .filter(appointment -> appointment.getScheduleDate().equals(scheduleDate))
        	            .findAny();

        	    if (anyAppointment.isPresent()) {
        	        int availableSlots = anyAppointment.get().getSlots();

        	        if (availableSlots == 0) {
        	            return ResponseEntity.ok("Slots are already full for the specified date.");
        	        } else {
        	            return ResponseEntity.ok(availableSlots + " slots remaining.");
        	        }
        	    }

            }
            else {
            	Schedule schedule = scheduleRepository.findById(scheduleId).orElse(null);

                if (schedule != null) {
                    int remainingSlots = schedule.getSlots();
                    return ResponseEntity.ok(remainingSlots + " slots remaining.");
                }
            }
        }

        return ResponseEntity.badRequest().body("Invalid scheduleId or scheduleDate.");
    }
    
    public void sendNotification(String email, Date scheduleDate, String status) {
        String subject = "Appointment Status";
        sendAppointmentNotification(email, subject, scheduleDate, status);
	}
    
	private void sendAppointmentNotification(String to, String subject, Date scheduleDate,  String status) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText("Your appointment scheduled on " + String.valueOf(scheduleDate) + " has been " + String.valueOf(status));
        javaMailSender.send(message);
    }
	
    public List<Appointment> getAppointmentsByPatientUserId(Long patientUserId) {
        return appointmentRepository.findAllByPatientUserId(patientUserId);
    }
    public List<Appointment> getAppointmentsByDoctorUserId(Long doctorUserId) {
        return appointmentRepository.findAllByDoctorUserId(doctorUserId);
    }
    

    public Long getScheduleId(Long appointmentId) {
        // Assuming appointmentRepository is an instance of JpaRepository<Appointment, Long>
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);

        return optionalAppointment.map(Appointment::getScheduleId).orElse(null);
    }


    public Appointment findAppointmentById(Long appointmentId) {
        return appointmentRepository.findById(appointmentId).orElse(null);
    }

    public List<Appointment> findAppointmentByDay(String scheduleDay) {
        return appointmentRepository.findByScheduleDay(scheduleDay);
    }
}
