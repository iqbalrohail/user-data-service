package com.rohailiqbal.user.service;

import com.rohailiqbal.user.data.transfer.object.ResponseDto;
import com.rohailiqbal.user.data.transfer.object.UserDto;
import com.rohailiqbal.user.data.transfer.object.UserMapper;
import com.rohailiqbal.user.domain.UserDomain;
import com.rohailiqbal.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This is a service class which contains the business logic for user's data.
 */
@Service
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RedisTemplate<String, UserDomain> redisTemplate;

    @Autowired
    private ValueOperations<String, UserDomain> valueOps;

    private static final String EXCEPTION = "Exception : ";
    private static final String OBJECT_ID_REGEX = "^[0-9a-fA-F]{24}$";

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Retrieves a list of all users from the database and returns them as a list of UserDto objects.
     * Maps the List of db objects (UserDomain) to Dto object (userDTo).
     *
     * @return A list of UserDto objects representing all users in the database
     */
    public ResponseEntity<Object> getAllUsers() {
        try {
            log.info("Call received at getAllUsers() method of UserService class");
            List<UserDomain> userDomains = this.userRepository.findAll();
            List<UserDto> userDtos = UserMapper.MapUserDomainListToUserDtoList(userDomains);
            log.trace(userDtos.toString());
            return ResponseEntity.ok(userDtos);
        } catch (Exception e) {
            log.error("An error occurred while retrieving all users: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves user information from cache and returns it in a ResponseEntity.
     * Checks the logged-in user's Id(user ID from principal) with the retrieving user ID
     *
     * @param userId The ID of the user to retrieve from cache.
     * @return A ResponseEntity containing the requested user information, or an error message if the user is not found or access is denied.
     * @throws Exception If an error occurs while retrieving the user information
     */
    public ResponseEntity<Object> whenUserInfoIsPresentInCache(String userId) throws Exception {
        log.info("getting object from cache");
        StringBuilder responseMessage = new StringBuilder();
        HttpStatus httpStatus;
        UserDomain existingUser = valueOps.get(userId);
        if (existingUser == null) {
            return existingUserNullResponse();
        }
        if (!Objects.equals(existingUser.getId(), userId)) {
            return permissionDeniedResponse(userId);
        }
        try {
            UserDto userDto = new UserDto(existingUser);
            httpStatus = HttpStatus.OK;
            return new ResponseEntity<>(userDto, httpStatus);
        } catch (Exception e) {
            responseMessage.append(EXCEPTION).append(e.getMessage());
            log.info(responseMessage.toString());
            throw new Exception(responseMessage.toString(), e);
        }
    }

    /**
     * Calls the whenUserInfoIsPresentInCache method if existing user is already in cache.
     * Checks for the current logged-in user from principal.
     * Finds the object from database by the username of principal(current user).
     * Retrieves a UserDomain object representing the user with the specified ID from the database.
     * Maps the db object (UserDomain) to Dto object (userDTo) by setting the values in setters.
     *
     * @param userId    the ID of the user to retrieve.
     * @param principal the authenticated user requesting the data.
     * @return a ResponseEntity containing either the retrieved UserDto or an error message.
     * @throws Exception if there is an error retrieving the user.
     */
    public ResponseEntity<Object> getUserById(String userId, Principal principal) throws Exception {
        StringBuilder responseMessage = new StringBuilder();
        HttpStatus httpStatus;
        ResponseDto responseDto;
        String username = principal.getName();
        log.info(username);
        if (!Pattern.matches(OBJECT_ID_REGEX, userId)) {
            return invalidObjectIdResponse(userId);
        }
        Set<String> keys = redisTemplate.keys(userId);
        if (!CollectionUtils.isEmpty(keys)) {
            return whenUserInfoIsPresentInCache(userId);
        }
        log.info("getting object from database");
        UserDomain existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            return existingUserNullResponse();
        }
        if (!Objects.equals(existingUser.getId(), userId)) {
            return permissionDeniedResponse(userId);
        }
        try {
            UserDomain userDomain = userRepository.findById(userId).orElse(null);
            if (userDomain == null) {
                return userNotFoundResponse(userId);
            }
            UserDto userDto = new UserDto(userDomain);
            saveUserToCache(userDomain);
            httpStatus = HttpStatus.OK;
            return new ResponseEntity<>(userDto, httpStatus);
        } catch (Exception e) {
            responseMessage.append(EXCEPTION).append(e.getMessage());
            log.info(responseMessage.toString());
            throw new Exception(responseMessage.toString(), e);
        }
    }

    /**
     * Maps the user data transfer object to db(domain) object.
     * Encodes the password using bCryptPasswordEncoder.
     * Adds a new user to the database using the mapped user data of userDomain object.
     * Saves the user data to cache.
     *
     * @param userDto the data transfer object containing user information
     * @return a ResponseEntity containing a ResponseDto with a success or error message and the HTTP status code
     * @throws Exception if there is an error while adding the user
     */
    public ResponseEntity<Object> addUser(UserDto userDto) throws Exception {
        log.info("Call received at addUser(UserDto userDto) method of UserService class");
        StringBuilder responseMessage = new StringBuilder();
        ResponseDto responseDto;
        HttpStatus httpStatus;
        if (userRepository.findByUsername(userDto.getUsername()) != null) {
            responseMessage = new StringBuilder("User is already registered with this username: ").append(userDto.getUsername());
            responseDto = new ResponseDto(responseMessage.toString());
            httpStatus = HttpStatus.CONFLICT;
            return new ResponseEntity<>(responseDto, httpStatus);
        }
        try {
            UserDomain userDomain = new UserDomain();
            userDomain.setUsername(userDto.getUsername());
            userDomain.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            userRepository.save(userDomain);
            saveUserToCache(userDomain);
            responseMessage.append("User have been added with id ").append(userDomain.getId());
            log.info(responseMessage.toString());
            responseDto = new ResponseDto(responseMessage.toString());
            httpStatus = HttpStatus.OK;
            return new ResponseEntity<>(responseDto, httpStatus);
        } catch (Exception e) {
            responseMessage.append(EXCEPTION).append(e.getMessage());
            log.info(responseMessage.toString());
            throw new Exception(responseMessage.toString(), e);
        }
    }

    /**
     * Checks for the current logged-in user from principal.
     * Finds the object from database by the username of principal(current user).
     * Updates a user by their ID using the provided UserDto object.
     * Updates the user info in cache.
     * Clears the user logged-in details from the SecurityContextHolder which logs-out the user.
     * Gets the user ID from userDto object and searches for the userDomain object based on this ID.
     * Maps the db object (userDomain) to data transfer object (userDto) by setters methods.
     *
     * @param userDto   the user data to be updated
     * @param principal the authenticated principal of the user making the request
     * @return a ResponseEntity containing the updated user data and the HTTP status
     * @throws Exception if an error occurs while updating the user
     */
    public ResponseEntity<Object> updateUserById(UserDto userDto, Principal principal) throws Exception {
        StringBuilder responseMessage = new StringBuilder();
        HttpStatus httpStatus;
        ResponseDto responseDto;
        String username = principal.getName();
        log.info(username);
        if (userDto.getId() == null || userDto.getId().isEmpty()) {
            responseMessage.append("Invalid user id : ").append(userDto.getId());
            responseDto = new ResponseDto(responseMessage.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(responseDto, httpStatus);
        }
        UserDomain existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            return existingUserNullResponse();
        }
        if (!Pattern.matches(OBJECT_ID_REGEX, userDto.getId())) {
            return invalidObjectIdResponse(userDto.getId());
        }
        if (!Objects.equals(existingUser.getId(), userDto.getId())) {
            return permissionDeniedResponse(userDto.getId());
        }
        try {
            UserDomain userDomain = userRepository.findById(userDto.getId()).orElse(null);
            if (userDomain == null) {
                return userNotFoundResponse(userDto.getId());
            }
            if (userRepository.findByUsername(userDto.getUsername()) != null) {
                responseMessage.append("User is already registered with this username: ").append(userDto.getUsername());
                responseDto = new ResponseDto(responseMessage.toString());
                httpStatus = HttpStatus.CONFLICT;
                return new ResponseEntity<>(responseDto, httpStatus);
            }
            userDomain.setUsername(userDto.getUsername());
            userDomain.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
            userRepository.save(userDomain);
            saveUserToCache(userDomain);
            SecurityContextHolder.clearContext();
            httpStatus = HttpStatus.OK;
            return new ResponseEntity<>(userDto, httpStatus);
        } catch (Exception e) {
            responseMessage.append(EXCEPTION).append(e.getMessage());
            log.info(responseMessage.toString());
            throw new Exception(responseMessage.toString(), e);
        }
    }

    /**
     * Deletes a user by their ID.
     * Checks for the user to only deletes his own info.
     * Deletes user info from cache if user data exists in cache.
     *
     * @param userId    the ID of the user to be deleted
     * @param principal the currently authenticated user
     * @return a ResponseEntity containing a ResponseDto and an HTTP status code
     * @throws Exception if an error occurs during the delete operation
     */
    public ResponseEntity<Object> deleteUserById(String userId, Principal principal) throws Exception {
        log.info("Call received at deleteUserById(String userId) method of UserService class");
        StringBuilder responseMessage = new StringBuilder();
        HttpStatus httpStatus;
        ResponseDto responseDto;
        String username = principal.getName();
        log.info(username);
        if (userId == null || userId.isEmpty()) {
            responseMessage.append("Invalid user id : ").append(userId);
            responseDto = new ResponseDto(responseMessage.toString());
            httpStatus = HttpStatus.BAD_REQUEST;
            return new ResponseEntity<>(responseDto, httpStatus);
        }
        UserDomain existingUser = userRepository.findByUsername(username);
        if (existingUser == null) {
            return existingUserNullResponse();
        }
        if (!Pattern.matches(OBJECT_ID_REGEX, userId)) {
            return invalidObjectIdResponse(userId);
        }
        if (!Objects.equals(existingUser.getId(), userId)) {
            return permissionDeniedResponse(userId);
        }
        try {
            userRepository.deleteById(userId);
            Set<String> keys = redisTemplate.keys(userId);
            if (!CollectionUtils.isEmpty(keys)) {
                redisTemplate.delete(userId);
            }
            SecurityContextHolder.clearContext();
            responseMessage.append("User details have been deleted with user-id ").append(userId);
            log.info(responseMessage.toString());
            responseDto = new ResponseDto(responseMessage.toString());
            httpStatus = HttpStatus.OK;
            return new ResponseEntity<>(responseDto, httpStatus);
        } catch (Exception e) {
            responseMessage.append(EXCEPTION).append(e.getMessage());
            log.info(responseMessage.toString());
            throw new Exception(responseMessage.toString(), e);
        }
    }

    /**
     * Saves a user to the Redis cache.
     *
     * @param userDomain the user to be saved to the cache
     */
    public void saveUserToCache(UserDomain userDomain) {
        redisTemplate.opsForValue().set(userDomain.getId(), userDomain);
    }

    /**
     * Generates a ResponseEntity object with NOT_FOUND status and a message indicating that a user with the specified ID was not found.
     *
     * @param userId the ID of the user that was not found
     * @return a ResponseEntity object with NOT_FOUND status and a message indicating that a user with the specified ID was not found
     */
    public ResponseEntity<Object> userNotFoundResponse(String userId) {
        HttpStatus httpStatus;
        ResponseDto responseDto;
        responseDto = new ResponseDto("Failed to find the user with ID : " + userId);
        httpStatus = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(responseDto, httpStatus);
    }

    /**
     * Generates a ResponseEntity object with INTERNAL_SERVER_ERROR status and a message indicating that an invalid ObjectId string was provided.
     *
     * @param userId the invalid ObjectId string that was provided
     * @return a ResponseEntity object with INTERNAL_SERVER_ERROR status and a message indicating that an invalid ObjectId string was provided
     */
    public ResponseEntity<Object> invalidObjectIdResponse(String userId) {
        StringBuilder responseMessage = new StringBuilder();
        HttpStatus httpStatus;
        ResponseDto responseDto;
        log.error("Invalid ObjectId string provided: {}", userId);
        responseMessage.append("Invalid ObjectId string provided: ").append(userId);
        responseDto = new ResponseDto(responseMessage.toString());
        httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(responseDto, httpStatus);
    }

    /**
     * Generates a ResponseEntity object with FORBIDDEN status and a message indicating that permission was denied for the specified user.
     *
     * @param userId the ID of the user for whom permission was denied
     * @return a ResponseEntity object with FORBIDDEN status and a message indicating that permission was denied for the specified user
     */
    public ResponseEntity<Object> permissionDeniedResponse(String userId) {
        StringBuilder responseMessage = new StringBuilder();
        HttpStatus httpStatus;
        ResponseDto responseDto;
        log.error("Permission denied");
        responseMessage.append("Permission denied ! with user ID : ").append(userId);
        responseDto = new ResponseDto(responseMessage.toString());
        httpStatus = HttpStatus.FORBIDDEN;
        return new ResponseEntity<>(responseDto, httpStatus);
    }

    /**
     * Generates a ResponseEntity object with NOT_FOUND status and a message indicating that a user was not found.
     *
     * @return a ResponseEntity object with NOT_FOUND status and a message indicating that a user was not found
     */
    public ResponseEntity<Object> existingUserNullResponse() {
        HttpStatus httpStatus;
        ResponseDto responseDto;
        responseDto = new ResponseDto("cannot find the user");
        httpStatus = HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(responseDto, httpStatus);
    }
}