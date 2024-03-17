package com.example.solarwatch.controller;

import com.example.solarwatch.controller.dto.CityDTO;
import com.example.solarwatch.controller.dto.LoginDTO;
import com.example.solarwatch.controller.dto.RegisterUserDTO;
import com.example.solarwatch.controller.dto.UserDTO;
import com.example.solarwatch.model.City;
import com.example.solarwatch.model.entity.Role;
import com.example.solarwatch.model.entity.UserEntity;
import com.example.solarwatch.model.payload.JwtResponse;
import com.example.solarwatch.repository.CityRepository;
import com.example.solarwatch.repository.SunsetAndSunriseRepository;
import com.example.solarwatch.repository.UserRepository;
import com.example.solarwatch.security.jwt.JwtUtils;
import com.example.solarwatch.service.ReportService;
import com.example.solarwatch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

@RestController
public class SolarWatchController {
    private final RestTemplate restTemplate;
    private final ReportService reportService;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public SolarWatchController(CityRepository cityRepository, SunsetAndSunriseRepository sunsetAndSunriseRepository, UserRepository userRepository, PasswordEncoder encoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.restTemplate = new RestTemplate();
        this.reportService = new ReportService(restTemplate, cityRepository, sunsetAndSunriseRepository);
        this.userService = new UserService(userRepository, encoder);
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/sunsetandrise")
    public ResponseEntity<?> getWeatherForecast(@RequestParam LocalDate date, @RequestParam String city) {
        System.out.println(date);
        var report = reportService.getReport(date, city);
        return ResponseEntity.ok(report);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterUserDTO registerUserDTO) {
        UserEntity user = userService.saveUser(registerUserDTO);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.username(), user.password())
        );

        String jwt = jwtUtils.generateJwtToken(authentication);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO userDTO) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(userDTO.userName(), userDTO.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .toList();

        return ResponseEntity
                .ok(new JwtResponse(jwt, userDetails.getUsername(), roles));
    }

    @GetMapping("/cities")
    public ResponseEntity<?> getAllCities() {
        List<City> cities = reportService.getCities();
        return ResponseEntity.ok(cities);
    }

    @PostMapping("/cities")
    public ResponseEntity<?> addCity(@RequestBody CityDTO cityDTO) {
        City city = reportService.addCity(cityDTO);
        return ResponseEntity.ok(city);
    }

    @PutMapping("/cities/{id}")
    public ResponseEntity<?> updateCity(@PathVariable Long id, @RequestBody CityDTO cityDTO) {
        City city = reportService.updateCity(id, cityDTO);
        return ResponseEntity.ok(city);
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity<?> deleteCity(@PathVariable Long id) {
        reportService.deleteCity(id);
        return ResponseEntity.ok().build();
    }
}
