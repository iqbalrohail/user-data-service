package com.rohailiqbal.user.data.transfer.object;

import com.rohailiqbal.user.domain.UserDomain;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The UserDto class represents a data transfer object for user information.
 * It contains the user's ID, username, and password.
 * This class is designed to be used to send or receive user information from the client.
 * The object of this class is mapped with UserDomain class 's object.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String username;
    private String password;

    public UserDto(UserDomain userDomain) {
        this.id = userDomain.getId();
        this.username = userDomain.getUsername();
        this.password = userDomain.getPassword();
    }
}
