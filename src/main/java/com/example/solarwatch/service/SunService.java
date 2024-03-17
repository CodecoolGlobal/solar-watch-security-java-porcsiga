package com.example.solarwatch.service;

import com.example.solarwatch.model.City;
import com.example.solarwatch.model.SunsetAndSunrise;
import com.example.solarwatch.repository.CityRepository;
import com.example.solarwatch.repository.SunsetAndSunriseRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
public class SunService {
    private final RestTemplate restTemplate;
    private final SunsetAndSunriseRepository sunsetAndSunriseRepository;
    private final GeocodingService geocodingService;

    public SunService(RestTemplate restTemplate, SunsetAndSunriseRepository sunsetAndSunriseRepository, GeocodingService geocodingService) {
        this.restTemplate = restTemplate;
        this.sunsetAndSunriseRepository = sunsetAndSunriseRepository;
        this.geocodingService = geocodingService;
    }

    public SunsetAndSunrise getSunTimes(double lat, double lon, LocalDate date, String cityName) {
        City city = geocodingService.getLocation(cityName);
        Optional<SunsetAndSunrise> existingReport = sunsetAndSunriseRepository.findByCityAndDate(city, date);
        if (existingReport.isPresent()) {
            return existingReport.get();
        }
        SunsetAndSunrise newReport = fetchAndSaveData(lat, lon, date, city);
        return newReport;
    }

    private SunsetAndSunrise fetchAndSaveData(double lat, double lon, LocalDate date, City city) {
        String url = String.format("https://api.sunrise-sunset.org/json?lat=%s&lng=%s&date=%s", lat, lon, date);
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("results")) {
            Map<String, String> results = (Map<String, String>) response.get("results");
            String sunrise = results.get("sunrise");
            String sunset = results.get("sunset");

            SunsetAndSunrise newReport = new SunsetAndSunrise();
            newReport.setSunrise(sunrise);
            newReport.setSunset(sunset);
            newReport.setDate(date);
            newReport.setCity(city);

            sunsetAndSunriseRepository.save(newReport);

            return newReport;
        } else {
            throw new RuntimeException("Unable to fetch sunrise and sunset data.");
        }
    }
}
