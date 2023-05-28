package com.rohailiqbal.user.service;

import com.rohailiqbal.user.domain.UserDomain;
import com.rohailiqbal.user.domain.UserPrincipal;
import com.rohailiqbal.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * This is the service class which implements the Spring Security UserDetailsService interface to load the user's authentication details from the database.
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads the user's authentication details from the database based on their username.
     *
     * @param username the username of the user
     * @return a UserDetails object containing the user's authentication details
     * @throws UsernameNotFoundException if the user is not found in the database
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDomain userDomain = userRepository.findByUsername(username);
        if (userDomain == null) {
            throw new UsernameNotFoundException("Failed to find User !");
        }
        return new UserPrincipal(userDomain);
    }
}