package com.tuan03.SpringbootJWT.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tuan03.SpringbootJWT.common.ERole;
import com.tuan03.SpringbootJWT.common.JWTUtils;
import com.tuan03.SpringbootJWT.dto.JwtResponse;
import com.tuan03.SpringbootJWT.dto.LoginRequest;
import com.tuan03.SpringbootJWT.dto.MessageResponse;
import com.tuan03.SpringbootJWT.dto.SignupRequest;
import com.tuan03.SpringbootJWT.entity.Role;
import com.tuan03.SpringbootJWT.entity.User;
import com.tuan03.SpringbootJWT.repository.RoleRepository;
import com.tuan03.SpringbootJWT.repository.UserRepository;
import com.tuan03.SpringbootJWT.service.UserDetailsImpl;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

        @Autowired
        AuthenticationManager authenticationManager;

        @Autowired
        UserRepository userRepository;

        @Autowired
        RoleRepository roleRepository;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        JWTUtils jwtUtils;

        @PostMapping("/login")
        public ResponseEntity<?> authenticateUser(@Validated @RequestBody LoginRequest loginRequest) {
                Authentication authentication = authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                loginRequest.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.genJWTToken(authentication);

                UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
                List<String> roles = userDetailsImpl.getAuthorities().stream().map(i -> i.getAuthority())
                                .collect(Collectors.toList());

                return ResponseEntity.ok(new JwtResponse(jwt, jwt, userDetailsImpl.getId(),
                                userDetailsImpl.getUsername(), userDetailsImpl.getEmail(), roles));
        }

        @PostMapping(value = "signup")
        public ResponseEntity<?> registerUser(@Validated @RequestBody SignupRequest signupRequest) {
                if (userRepository.existsByUsername(signupRequest.getUsername())) {
                        return ResponseEntity.badRequest()
                                        .body(new MessageResponse("Error: Username is already taken!"));
                }
                if (userRepository.existsByEmail(signupRequest.getEmail())) {
                        return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
                }
                User user = new User();
                user.setUsername(signupRequest.getUsername());
                user.setEmail(signupRequest.getEmail());
                user.setPassword(signupRequest.getPassword());
                Set<String> strRoles = signupRequest.getRole();
                Set<Role> roles = new HashSet<>();
                if (strRoles == null) {
                        Role role = roleRepository.findByName(ERole.USER)
                                        .orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
                        roles.add(role);
                } else {
                        strRoles.forEach(r -> {
                                switch (r) {
                                        case "admin":
                                                Role adminRole = roleRepository.findByName(ERole.ADMIN)
                                                                .orElseThrow(() -> new RuntimeException(
                                                                                "Error: Role is not found!"));
                                                roles.add(adminRole);
                                                break;
                                        case "mod":
                                                Role modRole = roleRepository.findByName(ERole.MANAGER)
                                                                .orElseThrow(() -> new RuntimeException(
                                                                                "Error: Role is not found!"));
                                                roles.add(modRole);
                                                break;
                                        default:
                                                Role role = roleRepository.findByName(ERole.USER)
                                                                .orElseThrow(() -> new RuntimeException(
                                                                                "Error: Role is not found!"));
                                                roles.add(role);
                                                break;
                                }
                        });
                }
                user.setRoles(roles);
                userRepository.save(user);
                return ResponseEntity.ok().body(new MessageResponse("User registered successfully!"));
        }

}
