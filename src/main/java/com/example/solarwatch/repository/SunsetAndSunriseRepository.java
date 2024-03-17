package com.example.solarwatch.repository;

import com.example.solarwatch.model.City;
import com.example.solarwatch.model.SunReport;
import com.example.solarwatch.model.SunsetAndSunrise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SunsetAndSunriseRepository extends JpaRepository<SunsetAndSunrise, Long> {

    Optional<SunsetAndSunrise> findByCityAndDate(City city, LocalDate date);
}
