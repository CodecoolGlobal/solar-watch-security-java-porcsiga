package com.example.solarwatch.repository;

import com.example.solarwatch.model.entity.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static java.lang.String.format;

@Repository
public class UserRepository {

    private final ConcurrentMap<String, UserEntity> users = new ConcurrentHashMap<>();

    public synchronized Optional<UserEntity> findUserByName(String userName) {
        return Optional.ofNullable(users.get(userName));
    }

    public synchronized void createUser(UserEntity user) {
        String userName = user.username();
        if (users.containsKey(userName)) {
            throw new IllegalArgumentException(format("user %s already exists", userName));
        }
        users.put(userName, user);
    }

    public void updateUser(UserEntity userEntity) {
        String userName = userEntity.username();

        if (!users.containsKey(userName)) {
            throw new IllegalArgumentException(format("User %s does not exist", userName));
        }

        users.put(userName, userEntity);
    }
}
