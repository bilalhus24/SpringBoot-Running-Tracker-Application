package com.example.demo.mapper;

import com.example.demo.entity.Run.Location;
import com.example.demo.entity.Run.Run;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class RunDto {
    private Long id;
    private Long userId;
    private double miles;
    private Location location;
    private LocalTime start;
    private LocalTime end;
    private Duration duration;
    private LocalDate date;
    private double averagePace;

    public RunDto(){

    }

    public RunDto(Run run) {
        this.id = run.getId();
        this.userId = run.getUserId();
        this.miles = run.getMiles();
        this.location = run.getLocation();
        this.start = run.getStart();
        this.end = run.getEnd();
        this.duration = Duration.between(run.getStart(), run.getEnd());
        this.date = run.getDate();
        this.averagePace = run.getMiles() / (this.duration.toMinutes() / 60.0); // Assuming averagePace is calculated as miles per hour
    }

}
