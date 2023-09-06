package com.photoapp.api.users.data;

import com.photoapp.api.users.shared.UserDto;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface UsersRepository extends CrudRepository<UserEntity, Long> {
	UserEntity findByEmail(String email);

    UserEntity findByUserId(String userId);
}
