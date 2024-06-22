package com.joel.authservice.api.configs.security;

import com.joel.authservice.domain.models.UserModel;
import com.joel.authservice.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    public static final String NOT_FOUND_WITH_USERNAME = "User Not found with username: ";
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(NOT_FOUND_WITH_USERNAME + username));
        return UserDetailsImpl.build(user);
    }

    public UserDetails loadUserById(UUID userId) throws AuthenticationCredentialsNotFoundException {
        UserModel userModel = userRepository.findById(userId)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("User Not Found with userId" + userId));
        return UserDetailsImpl.build(userModel);
    }
}
