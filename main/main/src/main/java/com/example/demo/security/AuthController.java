package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.mapper.UserDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.mailVerify.JwtTokenProvider;
import com.example.demo.security.mailVerify.MailVerification;
import com.example.demo.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailVerification mailVerification;

    private final Service service;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public AuthController(Service service, JwtTokenProvider jwtTokenProvider) {
        this.service = service;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createToken(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
            Optional<User> user = userRepository.findByEmail(authRequest.getEmail());
            if (!user.isPresent()) {
                throw new UserNotFoundException();
            }
            if (!user.get().getVerified()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not verified. Please verify before logging in");
            }
            final UserDetails userDetails = userService.loadUserByUsername(authRequest.getEmail());
            String jwt = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(jwt);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerNewUser(user);
        if (registeredUser != null) {
            mailVerification.sendVerificationEmail(registeredUser.getEmail(), jwtTokenProvider.generateVerificationToken(registeredUser.getEmail()));
            return ResponseEntity.ok("User registered successfully. " +
                    "Please check your email to verify your account before you are granted access to login.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User registration failed");
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        String email = jwtTokenProvider.validateToken(token);

        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token.");
        }

        UserDto userDto = service.getUserByEmail(email);
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        userDto.setVerified(true);
        service.updateUser(userDto);
        return ResponseEntity.ok("Email verified successfully.");
    }
}
