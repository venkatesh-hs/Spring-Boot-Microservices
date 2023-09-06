package com.photoapp.api.users.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.photoapp.api.users.data.UserEntity;
import com.photoapp.api.users.data.UsersRepository;
import com.photoapp.api.users.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {

	UsersRepository usersRepository;
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Override
	public UserDto createUser(UserDto userDetails) {

		userDetails.setUserId(UUID.randomUUID().toString());
		userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserEntity userEntity = modelMapper.map(userDetails, UserEntity.class);

		usersRepository.save(userEntity);

		return modelMapper.map(userEntity, UserDto.class);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity user = usersRepository.findByEmail(username);
		if (user == null)
			throw new UsernameNotFoundException(username);
		return new User(user.getEmail(), user.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {
		UserEntity user = usersRepository.findByEmail(email);
		if (user == null)
			throw new UsernameNotFoundException(email);
		return new ModelMapper().map(user, UserDto.class);
	}

	@Override
	public UserDto getUserById(String userId) {
		Optional<UserEntity> userEntity = Optional.ofNullable(usersRepository.findByUserId(userId));
		if (userEntity.isEmpty())
			throw new UsernameNotFoundException(String.valueOf(userId));
        return new ModelMapper().map(userEntity.get(), UserDto.class);
    }

}
