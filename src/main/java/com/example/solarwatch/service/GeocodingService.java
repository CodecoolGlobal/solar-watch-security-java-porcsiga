package com.example.solarwatch.service;

import com.example.solarwatch.model.City;
import com.example.solarwatch.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import java.util.Optional;

@Service
public class GeocodingService {
    private static final String API_KEY = "5db1b6ad6ff2ea418244d34219f09442";
    private final RestTemplate restTemplate;
    private final CityRepository cityRepository;


    public GeocodingService(RestTemplate restTemplate, CityRepository cityRepository) {
        this.restTemplate = restTemplate;
        this.cityRepository = cityRepository;
    }

    public City getLocation(String city) {
        Optional<City> existingCity = cityRepository.findByName(city);

        if (existingCity.isPresent()) {
            return existingCity.get();
        }

        String url = String.format("http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=%s&appid=%s", city, 1, API_KEY);
        City[] locations = restTemplate.getForObject(url, City[].class);

        if (locations != null && locations.length > 0) {
            City newCity = locations[0];
            City savedCity = cityRepository.save(newCity);
            return savedCity;
        } else {
            throw new RuntimeException("Unable to fetch location for city: " + city);
        }
    }
}
