package com.smartcanteen.security;

import com.smartcanteen.login.enity.ERole;
import com.smartcanteen.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Check and create roles if they don't exist
        if (roleRepository.findByName(ERole.ROLE_STUDENT).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_STUDENT));
        }
        if (roleRepository.findByName(ERole.ROLE_CANTEEN_MANAGER).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_CANTEEN_MANAGER));
        }
        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
        }
        if (roleRepository.findByName(ERole.ROLE_NGO).isEmpty()) { 
            roleRepository.save(new Role(ERole.ROLE_NGO));
        }
    }
}