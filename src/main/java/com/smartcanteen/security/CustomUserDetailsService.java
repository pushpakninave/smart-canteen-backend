package com.smartcanteen.security;

import com.smartcanteen.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartcanteen.login.enity.ERole;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        // Get the String name of the ERole enum (e.g., "ROLE_NGO", "ROLE_STUDENT")
        String roleNameFromDb = user.getRole().getName().name(); // This now directly gives "ROLE_NGO"

        // Convert the String name to the ERole enum constant (e.g., ERole.ROLE_NGO)
        ERole eRole = ERole.fromName(roleNameFromDb); // fromName handles both prefixed and non-prefixed input

        // Build UserDetailsImpl with the correct format for GrantedAuthority
        // Since ERole.name() now returns "ROLE_NGO", we pass it directly.
        return UserDetailsImpl.build(user, Collections.singletonList(new SimpleGrantedAuthority(eRole.name())));
    }
}