package com.photoapp.api.users.ui.model;

import lombok.Data;

import java.util.List;

@Data
public class UserResponseModel {
    private String firstName;
    private String lastName;
    private String email;
    private String UserId;
    private List<AlbumResponseModel> albums;
}
