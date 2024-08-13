package com.example.demo.Tests;

import com.example.demo.controller.UserController;
import com.example.demo.entity.Role;
import com.example.demo.mapper.UserDto;
import com.example.demo.security.JwtService;
import com.example.demo.security.UserService;
import com.example.demo.service.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Service service;

    private UserDto userDto;
    private List<UserDto> userDtoList;

    @BeforeEach
    public void setup() {
        userDto = new UserDto(1L, "User", "test@example.com", "password", 10, 50.0, Role.ROLE_USER, true, null);
        userDtoList = Arrays.asList(userDto);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetProfile() throws Exception {
        Mockito.when(service.getUserByEmail(any(String.class))).thenReturn(userDto);

        mockMvc.perform(get("/api/users/profile")
                        .principal(() -> "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetUserById() throws Exception {
        Mockito.when(service.getUserById(anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllUsers() throws Exception {
        Mockito.when(service.getAllUsers()).thenReturn(userDtoList);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUserRoleEndpoint() throws Exception {
        mockMvc.perform(get("/api/users/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Access granted for USER role"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddUser() throws Exception {
        Mockito.when(service.createUser(any(UserDto.class))).thenReturn(userDto);

        String userJson = "{\"name\":\"User\",\"email\":\"test@example.com\",\"password\":\"password\",\"totalRuns\":10,\"totalDistance\":50.0,\"role\":\"ROLE_USER\",\"verified\":true}";

        mockMvc.perform(post("/api/users/add")
                        .content(userJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUser() throws Exception {
        Mockito.when(service.updateUser(any(UserDto.class))).thenReturn(userDto);

        String userJson = "{\"name\":\"User\",\"email\":\"test@example.com\",\"password\":\"password\",\"totalRuns\":10,\"totalDistance\":50.0,\"role\":\"ROLE_USER\",\"verified\":true}";

        mockMvc.perform(put("/api/users/1/update")
                        .content(userJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/1/delete"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted"));
    }
}
