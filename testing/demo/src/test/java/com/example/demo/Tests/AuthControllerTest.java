package com.example.demo.Tests;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.AuthController;
import com.example.demo.security.AuthRequest;
import com.example.demo.security.JwtService;
import com.example.demo.security.UserService;
import com.example.demo.security.mailVerify.JwtTokenProvider;
import com.example.demo.security.mailVerify.MailVerification;
import com.example.demo.service.Service;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @MockBean
    private MailVerification mailVerification;

    @MockBean
    private Service service;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    void testCreateToken() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setVerified(true);

        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));
    }

    @Test
    @WithMockUser
    void testRegisterUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userService.registerNewUser(any(User.class))).thenReturn(user);
        when(jwtTokenProvider.generateVerificationToken(anyString())).thenReturn("verification-token");

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully. Please check your email to verify your account before you are granted access to login."));
    }

    @Test
    @WithMockUser
    void testVerifyEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");

        when(jwtTokenProvider.validateToken(anyString())).thenReturn("test@example.com");
        when(service.getUserByEmail(anyString())).thenReturn(userDto);

        mockMvc.perform(get("/api/auth/verify")
                        .param("token", "verification-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Email verified successfully."));
    }

    @Test
    @WithMockUser
    void testCreateTokenWithInvalidCredentials() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Invalid credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    @WithMockUser
    void testRegisterUserFail() throws Exception {
        when(userService.registerNewUser(any(User.class))).thenReturn(null);

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@example.com\", \"password\":\"password\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("User registration failed"));
    }

    @Test
    @WithMockUser
    void testVerifyEmailInvalidToken() throws Exception {
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/auth/verify")
                        .param("token", "invalid-token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid or expired token."));
    }

    @Test
    @WithMockUser
    void testVerifyEmailUserNotFound() throws Exception {
        when(jwtTokenProvider.validateToken(anyString())).thenReturn("test@example.com");
        when(service.getUserByEmail(anyString())).thenReturn(null);

        mockMvc.perform(get("/api/auth/verify")
                        .param("token", "verification-token"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found."));
    }
}
