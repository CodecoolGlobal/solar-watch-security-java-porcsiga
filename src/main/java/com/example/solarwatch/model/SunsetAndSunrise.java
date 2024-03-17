package com.example.solarwatch.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class SunsetAndSunrise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String sunset;
    private String sunrise;
    @ManyToOne
    private City city;
    private LocalDate date;

    public String getSunset() {
        return sunset;
    }

    public String getSunrise() {
        return sunrise;
    }

    public City getCity() {
        return city;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
