package com.smartcanteen.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartcanteen.entity.User;
import com.smartcanteen.repository.UserRepository;
import com.smartcanteen.security.utils.UserDetailsImpl;
import com.smartcanteen.utility.enums.ERole;

@Service
public class CustomUserDetailsService implements UserDetailsService{

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        String roleNameFromDb = user.getRole().getName().name(); // This now directly gives "ROLE_NGO"

        // Convert the String name to the ERole enum constant (e.g., ERole.ROLE_NGO)
        ERole eRole = ERole.fromName(roleNameFromDb); // fromName handles both prefixed and non-prefixed input

        // Build UserDetailsImpl with the correct format for GrantedAuthority
        // Since ERole.name() now returns "ROLE_NGO", we pass it directly.
        return UserDetailsImpl.build(user, Collections.singletonList(new SimpleGrantedAuthority(eRole.name())));
    }
}