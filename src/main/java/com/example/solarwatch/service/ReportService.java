package com.example.solarwatch.service;

import com.example.solarwatch.controller.dto.CityDTO;
import com.example.solarwatch.model.City;
import com.example.solarwatch.model.Report;
import com.example.solarwatch.model.SunReport;
import com.example.solarwatch.model.SunsetAndSunrise;
import com.example.solarwatch.repository.CityRepository;
import com.example.solarwatch.repository.SunsetAndSunriseRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {
    private final RestTemplate restTemplate;
    private final CityRepository cityRepository;
    private final SunsetAndSunriseRepository sunsetAndSunriseRepository;
    private final GeocodingService geocodingService;
    private final SunService sunService;

    public ReportService(RestTemplate restTemplate, CityRepository cityRepository, SunsetAndSunriseRepository sunsetAndSunriseRepository) {
        this.restTemplate = restTemplate;
        this.cityRepository = cityRepository;
        this.sunsetAndSunriseRepository = sunsetAndSunriseRepository;
        this.geocodingService = new GeocodingService(restTemplate, cityRepository);
        this.sunService = new SunService(restTemplate, sunsetAndSunriseRepository, geocodingService);
    }

    public Report getReport(LocalDate date, String city) {
        City location = geocodingService.getLocation(city);
        SunsetAndSunrise sunReport = sunService.getSunTimes(location.getLatitude(), location.getLongitude(), date, city);
        return new Report(date, sunReport.getSunrise(), sunReport.getSunset(), city);
    }

    public City addCity(CityDTO cityDTO) {
        City city = new City();
        city.setName(cityDTO.name());
        city.setCountry(cityDTO.country());
        city.setState(cityDTO.state());
        city.setLatitude(cityDTO.latitude());
        city.setLongitude(cityDTO.longitude());
        cityRepository.save(city);
        return city;
    }

    public City updateCity(long id, CityDTO cityDTO) {
        City city = cityRepository.findById(id).get();
        city.setName(cityDTO.name());
        city.setCountry(cityDTO.country());
        city.setState(cityDTO.state());
        city.setLatitude(cityDTO.latitude());
        city.setLongitude(cityDTO.longitude());
        cityRepository.save(city);
        return city;
    }

    public List<City> getCities() {
        return cityRepository.findAll();
    }

    public void deleteCity(Long id) {
        City city = cityRepository.findById(id).get();
        cityRepository.delete(city);
    }
}
