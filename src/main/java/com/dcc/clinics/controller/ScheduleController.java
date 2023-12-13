package com.dcc.clinics.controller;

import com.dcc.clinics.model.Schedule;
import com.dcc.clinics.service.ScheduleService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.List;

@RestController
@CrossOrigin(origins = "https://docclickconnect.vercel.app/")
public class ScheduleController {

    private final ScheduleService scheduleService;


    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @CrossOrigin(origins = "http://localhost:3000") 
    @PostMapping("/schedule")
    public ResponseEntity<String> addSchedule(
        @RequestParam String name, // Add clinic name parameter
        @RequestParam Long doctorUserId,
        @RequestParam String scheduleDay,
        @RequestParam Time startTime,
        @RequestParam Time endTime,
        @RequestParam Integer slots
    ) {
        String result = scheduleService.saveSchedule(name, doctorUserId, scheduleDay, startTime, endTime, slots);

        if (result.startsWith("Schedule saved")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }



    @PutMapping("/schedule/{scheduleId}")
    public ResponseEntity<String> updateSchedule(
        @PathVariable Long scheduleId,
        @RequestParam String scheduleDay,
        @RequestParam Time startTime,
        @RequestParam Time endTime,
        @RequestParam Integer slots
    ) {
        String result = scheduleService.updateSchedule(scheduleId, scheduleDay, startTime, endTime, slots);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(@PathVariable Long scheduleId) {
        String result = scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(result);
    }

    // Get a schedule by name
    @GetMapping("/docsched/{doctorId}")
    public ResponseEntity<List<Schedule>> searchSchedulesByDoctorId(@PathVariable Long doctorId) {
        List<Schedule> schedules = scheduleService.searchByDoctorUserId(doctorId);
        return ResponseEntity.ok(schedules);
    }
    // Get all schedules
    @GetMapping("/schedules")
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        List<Schedule> schedules = scheduleService.getAllSchedules();
        if (schedules != null && !schedules.isEmpty()) {
            return ResponseEntity.ok(schedules);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get all by date
    @GetMapping("/findScheduleDate/{day}")
    public ResponseEntity<List<Schedule>> searchScheduleByDay(@PathVariable("day") String day){
        List<Schedule> schedules = scheduleService.searchByScheduleDay(day);
        if (schedules != null && !schedules.isEmpty()) {
            return ResponseEntity.ok(schedules);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<Schedule> findScheduleById(@PathVariable("scheduleId") Long scheduleId) {
        return ResponseEntity.ok(scheduleService.findScheduleById(scheduleId));
    }

}

