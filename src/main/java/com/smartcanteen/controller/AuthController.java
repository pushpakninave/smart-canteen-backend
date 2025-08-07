package com.smartcanteen.controller;

import com.smartcanteen.login.enity.ERole;
import com.smartcanteen.login.enity.JwtResponse;
import com.smartcanteen.login.enity.LoginRequest;
import com.smartcanteen.login.enity.RegisterRequest;
import com.smartcanteen.repository.RoleRepository;
import com.smartcanteen.repository.UserRepository;
import com.smartcanteen.security.CustomUserDetailsService;
import com.smartcanteen.security.JwtTokenUtil;
import com.smartcanteen.security.Role;
import com.smartcanteen.security.User;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil,
                          CustomUserDetailsService customUserDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found after authentication."));

        String jwt = jwtTokenUtil.generateToken(userDetails);

        // Role name for frontend will now directly be the enum name (e.g., "ROLE_NGO")
        String roleNameForFrontend = user.getRole().getName().name();

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roleNameForFrontend));
    }

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new JwtResponse(null, null, null, null, "Username already taken!"));
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new JwtResponse(null, null, null, null, "Email already in use!"));
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        ERole eRole;
        if (registerRequest.getRole() != null) {
            // Use ERole.fromName for robust conversion during registration
            // This handles cases where frontend sends "student", "manager", "admin", "ngo"
            // and converts them to "ROLE_STUDENT", "ROLE_MANAGER", etc.
            eRole = ERole.fromName(registerRequest.getRole());
        } else {
            eRole = ERole.ROLE_STUDENT; // Default role
        }

        Role assignedRole = roleRepository.findByName(eRole)
                .orElseThrow(() -> new RuntimeException("Error: Role " + eRole + " is not found."));
        user.setRole(assignedRole);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
        String jwt = jwtTokenUtil.generateToken(userDetails);

        // Role name for frontend will now directly be the enum name (e.g., "ROLE_NGO")
        String roleNameForFrontend = user.getRole().getName().name();

        return ResponseEntity.status(HttpStatus.CREATED).body(new JwtResponse(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roleNameForFrontend
        ));
    }

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content. Anyone can access this.";
    }

    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public String studentBoard() {
        return "Student Board Content.";
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('CANTEEN_MANAGER') or hasRole('ADMIN')")
    public String managerBoard() {
        return "Manager Board Content.";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminBoard() {
        return "Admin Board Content.";
    }
}