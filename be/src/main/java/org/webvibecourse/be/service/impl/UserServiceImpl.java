package org.webvibecourse.be.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.webvibecourse.be.entity.User;
import org.webvibecourse.be.repository.UserRepository;
import org.webvibecourse.be.service.UserService;

import java.util.Optional;

/**
 * =====================================================================================
 * -------------------------
 * UserServiceImpl
 * This service implements Spring Security's UserDetailsService to load user
 * information from the database when authentication occurs.
 * <p>
 * - Spring Security uses loadUserByUsername() to fetch the user from DB
 * based on the provided username (in this case → email).
 * <p>
 * - The returned User entity must implement UserDetails, allowing Spring
 * to read password, roles, and account status for authentication.
 * <p>
 * Responsibilities:
 * ✔ Load user by email for authentication
 * ✔ Throw exception if user does not exist
 * ✔ Provide UserDetails object used by AuthenticationManager
 * <p>
 * ==> This service is a core component for Spring Security login process.
 * =====================================================================================
 */


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * Load user information by username (email).
     *
     * @param username
     * @return UserDetails (User entity implementing UserDetails)
     * @throws UsernameNotFoundException UsernameNotFoundException if user is not found in DB
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        // If user is missing → authentication fails
        return user.orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
    }
}
