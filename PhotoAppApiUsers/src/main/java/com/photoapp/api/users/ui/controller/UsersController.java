package com.photoapp.api.users.ui.controller;

import com.photoapp.api.users.data.AlbumsServiceClient;
import com.photoapp.api.users.service.UsersService;
import com.photoapp.api.users.shared.UserDto;
import com.photoapp.api.users.ui.model.AlbumResponseModel;
import com.photoapp.api.users.ui.model.CreateUserRequestModel;
import com.photoapp.api.users.ui.model.CreateUserResponseModel;
import com.photoapp.api.users.ui.model.UserResponseModel;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@Consumes(value = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@Produces(value = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class UsersController {

    @Autowired
    private Environment env;

    @Autowired
    private AlbumsServiceClient albumsServiceClient;
    //private RestTemplate restTemplate;

    @Autowired
    UsersService usersService;

    public UsersController() {
    }

    @GetMapping("/status/check")
    public String status() {
        return "Working at port : " + env.getProperty("local.server.port") + " with token secret : " + env.getProperty("token.secret");
    }

    @PostMapping
    public ResponseEntity<CreateUserResponseModel> createUser(@Valid @RequestBody CreateUserRequestModel userDeatils) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(userDeatils, UserDto.class);

        CreateUserResponseModel user = modelMapper.map(usersService.createUser(userDto), CreateUserResponseModel.class);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseModel> getUser(@PathVariable("id") String userId) throws Throwable {
        UserDto user = usersService.getUserById(userId);

        //Below commented is the usage of Rest Template to communicate with the other MicroServices
        /*String albumsUrl = String.format(Objects.requireNonNull(env.getProperty("albums.url")), userId);
        ResponseEntity<List<AlbumResponseModel>> albumsResponseList = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        List<AlbumResponseModel> albums = albumsResponseList.getBody();
        */

        //Usage of Feign client
        List<AlbumResponseModel> albums = albumsServiceClient.getAlbums(userId);
        user.setAlbums(albums);
        return ResponseEntity.ok().body(
                new ModelMapper().map(user, UserResponseModel.class)
        );
    }

}
