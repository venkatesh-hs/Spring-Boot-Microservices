package com.photoapp.api.users.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.photoapp.api.users.shared.UserDto;

import java.math.BigInteger;

public interface UsersService extends UserDetailsService {
    UserDto createUser(UserDto userDetails);

    UserDto getUserDetailsByEmail(String email);

    UserDto getUserById(String userId) throws Throwable;
}
