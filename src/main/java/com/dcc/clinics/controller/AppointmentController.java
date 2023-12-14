package com.dcc.clinics.controller;

import com.dcc.clinics.model.Appointment;
import com.dcc.clinics.service.AppointmentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "https://docclickconnect.vercel.app")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appointment")
    public ResponseEntity<String> addAppointment(
            @RequestParam Long patientId,
            @RequestParam Long scheduleId,
            @RequestParam Date scheduleDate,
            @RequestParam String status) {
        String result = appointmentService.saveAppointment(patientId, scheduleId, scheduleDate, status);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/appointment/{appointmentId}")
    public ResponseEntity<String> updateAppointment(
            @PathVariable Long appointmentId,
            @RequestParam Date scheduleDate
    ) {
        return ResponseEntity.ok(appointmentService.updateAppointment(appointmentId, scheduleDate));
    }
    
    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatientUserId(@RequestParam Long patientUserId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPatientUserId(patientUserId);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/docappointments")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctorUserId(@RequestParam Long doctorUserId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorUserId(doctorUserId);
        return ResponseEntity.ok(appointments);
    }
    
    @GetMapping("/checkSlots/{scheduleId}/{scheduleDate}")
    public ResponseEntity<String> checkSlots(
    		@PathVariable Long scheduleId,
    		@PathVariable Date scheduleDate) {
        return appointmentService.checkSlotsAvailability(scheduleId, scheduleDate);
    }

    @GetMapping("/getScheduleId/{appointmentId}")
    public ResponseEntity<Long> getScheduleId(@PathVariable Long appointmentId) {
        Long scheduleId = appointmentService.getScheduleId(appointmentId);

        if (scheduleId != null) {
            return ResponseEntity.ok(scheduleId);
        } else {
            // Handle the case when the schedule is not found
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("appointmentChange/{appointmentId}")
    public ResponseEntity<String> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestParam String newStatus) {
        String result = appointmentService.updateAppointmentStatus(appointmentId, newStatus);

        if (result.startsWith("Appointment status updated")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }
    
    @PutMapping("/cancelAppointment/{appointmentId}")
    public ResponseEntity<String> cancelAppointment(@PathVariable Long appointmentId) {
        String result = appointmentService.cancelAppointment(appointmentId);
        
        if (result.equals("Appointment status updated to cancelled")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    @DeleteMapping("/appointment/{appointmentId}")
    public ResponseEntity<String> deleteAppointment(@PathVariable("appointmentId") Long appointmentId) {
        return ResponseEntity.ok(appointmentService.deleteAppointment(appointmentId));
    }

    @GetMapping("/allappointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        if (appointments != null && !appointments.isEmpty()) {
            return ResponseEntity.ok(appointments);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Appointment> getAppointment(@PathVariable("appointmentId") Long appointmentId) {
        return ResponseEntity.ok(appointmentService.findAppointmentById(appointmentId));
    }

    @GetMapping("/appointment/day/{scheduleDay}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDay(@PathVariable("scheduleDay") String scheduleDay) {
        List<Appointment> appointmentsOnDay = appointmentService.findAppointmentByDay(scheduleDay);
        if(appointmentsOnDay != null && !appointmentsOnDay.isEmpty()) {
            return ResponseEntity.ok(appointmentsOnDay);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/Callback")
    public String catchCode(@RequestParam String code) {
        System.out.println(code);
        return appointmentService.catchCode(code);
    }

}
