package com.rohailiqbal.user.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.io.Serializable;

/**
 * The UserDomain class represents a domain(db) object for user information.
 * It contains the user's ID(autogenerated object_id), username, and password.
 * This class is designed to be used to interact with the database and store/retrieve user information.
 * Values are set by UserDto object in this class.
 */
@Data
@NoArgsConstructor
@Document(collection = "User")
public class UserDomain implements Serializable {
    @Id
    @Field(targetType = FieldType.OBJECT_ID)
    private String id;
    private String username;
    private String password;
}