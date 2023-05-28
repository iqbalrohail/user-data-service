package com.rohailiqbal.user.controller;

import com.rohailiqbal.user.data.transfer.object.UserDto;
import com.rohailiqbal.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * The UserController class is responsible for handling REST API requests related to user management.
 * It is annotated with @RestController to indicate that it will handle HTTP requests and responses.
 * The @RequestMapping("/user") annotation specifies that all requests starting with "/user" will be handled by this controller.
 * This class utilizes the UserService class to handle user-related logic.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles GET requests to retrieve all users.
     * This is a test Api.
     *
     * @return - A List of UserDto objects that represent all users in the system.
     */
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET call have been received at /user");
        return userService.getAllUsers();
    }

    /**
     * Handles GET requests to retrieve a single user by their ID.
     * Caches the response using the 'user_cache' cache.
     *
     * @param id        the id of the user to get
     * @param principal the authenticated principal object
     * @return a ResponseEntity with the UserDto of the requested user, or an error message
     * @throws Exception if there is an error processing the request
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") String id, Principal principal) throws Exception {
        log.info("Get call have been received at /user with id: {}", id);
        return userService.getUserById(id, principal);
    }

    /**
     * Handles POST requests to add a new user to the system.
     *
     * @param userDto the user data transfer object containing the user information
     * @return a ResponseEntity containing a ResponseDto with a success or error message
     * @throws Exception if there is an error adding the user
     */
    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody UserDto userDto) throws Exception {
        log.info("POST call have been received at /user with DTO: {}", userDto);
        return userService.addUser(userDto);
    }

    /**
     * Handles PUT requests to update an existing user in the system.
     *
     * @param userDto   the user DTO containing the updated user information.
     * @param principal the Principal object representing the authenticated user.
     * @return a ResponseEntity with a ResponseDto and an HTTP status indicating the result of the operation.
     * @throws Exception if an error occurs while updating the user.
     */
    @PutMapping
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, Principal principal) throws Exception {
        log.info("PUT call have been received at /user with DTO: {}", userDto);
        return userService.updateUserById(userDto, principal);
    }

    /**
     * Handles DELETE requests to delete an existing user by ID.
     *
     * @param id        the ID of the user to be deleted
     * @param principal the authenticated user making the request
     * @return ResponseEntity with a ResponseDto containing the message indicating the success or failure of the request
     * @throws Exception if there is an error while processing the request
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable("id") String id, Principal principal) throws Exception {
        log.info("DELETE call have been received at /user with id: {}", id);
        return userService.deleteUserById(id, principal);
    }
}