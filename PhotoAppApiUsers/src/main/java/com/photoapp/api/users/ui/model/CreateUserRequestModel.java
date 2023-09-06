package com.photoapp.api.users.ui.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CreateUserRequestModel {
	@NotNull(message = "First Name can't be Null")
	@Size(min=2, message= "First Name can't be less than 2 charaters")
	private String firstName;
	
	@NotNull(message = "Last Name can't be Null")
	@Size(min=2, message= "Last Name can't be less than 2 charaters")
	private String lastName;
	
	@NotNull(message = "Password can't be Null")
	@Size(min = 8, message = "Password must be greater than 8 characters")
	private String password;
	
	@NotNull(message = "Email can't be Null")
	@Email
	private String email;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
