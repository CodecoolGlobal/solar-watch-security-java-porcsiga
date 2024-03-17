package com.example.solarwatch.model;

import java.time.LocalDate;

public record Report (LocalDate date, String sunrise, String sunset, String city){
}
