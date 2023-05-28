package com.rohailiqbal.user.data.transfer.object;

import com.rohailiqbal.user.domain.UserDomain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class to convert UserDomain objects to UserDto objects.
 */
public class UserMapper {
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     *  Exclude the password field from the mapping process
     */
    static {
        mapper.addMixIn(UserDomain.class, UserDtoMixin.class);
    }

    /**
     * Converts a UserDomain object to a UserDto object.
     *
     * @param userDomain the UserDomain object to convert
     * @return the resulting UserDto object
     */
    public static UserDto MapUserDomaintoUserDto(UserDomain userDomain) {
        return mapper.convertValue(userDomain, UserDto.class);
    }

    /**
     * Converts a list of UserDomain objects to a list of UserDto objects.
     *
     * @param userDomains the list of UserDomain objects to convert
     * @return the resulting list of UserDto objects
     */
    public static List<UserDto> MapUserDomainListToUserDtoList(List<UserDomain> userDomains) {
        return userDomains.stream()
                .map(UserMapper::MapUserDomaintoUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Mixin class to exclude the password field from UserDomain to UserDto mapping.
     */
    @JsonIgnoreProperties(value = {"password"})
    abstract static class UserDtoMixin {
    }
}