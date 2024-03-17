package com.example.solarwatch.service;

import com.example.solarwatch.controller.dto.RegisterUserDTO;
import com.example.solarwatch.controller.dto.UserDTO;
import com.example.solarwatch.model.entity.Role;
import com.example.solarwatch.model.entity.UserEntity;
import com.example.solarwatch.repository.UserRepository;
import com.example.solarwatch.security.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public UserEntity findCurrentUser() {
        UserDetails contextUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        String username = contextUser.getUsername();
        return userRepository.findUserByName(username)
                .orElseThrow(() -> new IllegalArgumentException(format("could not find user %s in the repository", username)));

    }

    public void changeRole(UserEntity user, Role role) {
        userRepository.updateUser(new UserEntity(user.username(), user.password(), role));
    }

    public UserEntity saveUser(RegisterUserDTO registerUserDTO) {
        String encodedPassword = encoder.encode(registerUserDTO.password());
        UserEntity user = new UserEntity(registerUserDTO.userName(), encodedPassword, Role.ROLE_ADMIN);
        userRepository.createUser(user);
        return new UserEntity(registerUserDTO.userName(), registerUserDTO.password(), Role.ROLE_ADMIN);
    }
}

