package com.example.solarwatch.service;

import com.example.solarwatch.model.City;
import com.example.solarwatch.model.Location;
import com.example.solarwatch.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class GeocodingServiceTest {
    GeocodingService geocodingService;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
        CityRepository cityRepository = Mockito.mock(CityRepository.class);
        this.geocodingService = new GeocodingService(restTemplate, cityRepository);

        Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(Location[].class)))
                .thenReturn(new Location[]{new Location("51.5073219", "-0.1276474")});
    }

    @Test
    void getLocation() {
        City res = geocodingService.getLocation("London");
        Location expected = new Location("51.5073219", "-0.1276474");

        assertEquals(expected.lat(), res.getLatitude());
    }
}