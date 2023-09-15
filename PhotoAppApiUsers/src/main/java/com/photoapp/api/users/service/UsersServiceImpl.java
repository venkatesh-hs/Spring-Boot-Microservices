package com.photoapp.api.users.service;

import com.photoapp.api.users.data.AlbumsServiceClient;
import com.photoapp.api.users.data.UserEntity;
import com.photoapp.api.users.data.UsersRepository;
import com.photoapp.api.users.shared.UserDto;
import com.photoapp.api.users.ui.model.AlbumResponseModel;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UsersServiceImpl implements UsersService {

	UsersRepository usersRepository;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private AlbumsServiceClient albumsServiceClient;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Environment env;

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
		UserDto userDto = new ModelMapper().map(userEntity.get(), UserDto.class);

		//Below commented is the usage of Rest Template to communicate with the other MicroServices
        String albumsUrl = String.format(Objects.requireNonNull(env.getProperty("albums.url")), userId);
        ResponseEntity<List<AlbumResponseModel>> albumsResponseList = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        List<AlbumResponseModel> albums = albumsResponseList.getBody();

		//Usage of Feign client
		log.debug("Before calling albums Microservice");
		//List<AlbumResponseModel> albums = albumsServiceClient.getAlbums(userId);
		log.debug("After calling albums Microservice");
        /*try {
            albums = albumsServiceClient.getAlbums(userId);
        } catch (FeignException e) {
            log.error(e.getMessage());
        }*/
		userDto.setAlbums(albums);

        return new ModelMapper().map(userDto, UserDto.class);
    }

}
