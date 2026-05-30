package com.example.entryPool.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    private LocalDate date;
    private boolean isHoliday;
    private LocalTime openTime;
    private LocalTime closeTime;
}
