package com.rohailiqbal.user.domain;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * A class that implements the UserDetails interface to represent a user.
 */

@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private static final String MY_USER_ROLE = "USER";
    private final UserDomain userDomain;

    /**
     * Returns a collection of GrantedAuthority objects that represent the user's role.
     *
     * @return a collection of GrantedAuthority objects
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(MY_USER_ROLE));
    }

    /**
     * Checks for the password of the user.
     *
     * @return the password of the user
     */
    @Override
    public String getPassword() {
        return userDomain.getPassword();
    }

    /**
     * Checks for the user with the provided username.
     *
     * @return the username of the user
     */
    @Override
    public String getUsername() {
        return userDomain.getUsername();
    }

    /**
     * Checks whether the user's account is expired or not.
     *
     * @return {@code true} if the account is non-expired, {@code false} otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Checks whether a user's account is locked or not.
     *
     * @return {@code true}  if the account is non-locked, {@code false}  otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Checks whether the user credentials are expired or not.
     *
     * @return {@code true} if the credentials are non-expired, {@code false} otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Checks whether a user is enabled or not.
     *
     * @return {@code true} if the user is enabled {@code false} otherwise
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
