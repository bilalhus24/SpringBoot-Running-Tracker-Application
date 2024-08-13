package com.example.demo.Tests;

import com.example.demo.controller.RunController;
import com.example.demo.entity.Role;
import com.example.demo.entity.Run.Location;
import com.example.demo.mapper.RunDto;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RunController.class)
@Import(TestSecurityConfig.class)  // Import the test security configuration
public class RunControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private Service service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    private RunDto runDto;
    private List<RunDto> runDtoList;
    private UserDto userDto;

    @BeforeEach
    public void setup() {
        runDto = new RunDto();
        runDto.setId(1L);
        runDto.setUserId(1L);
        runDto.setMiles(5.0);
        runDto.setLocation(Location.INDOOR);
        runDto.setStart(LocalTime.now());
        runDto.setEnd(LocalTime.now().plusHours(1));
        runDto.setDate(LocalDate.now());
        runDtoList = Arrays.asList(runDto);

        userDto = new UserDto(1L, "User", "test@example.com", "password", 10, 50.0, Role.ROLE_USER, true, null);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetRunsForCurrentUser() throws Exception {
        Mockito.when(service.getUserById(anyLong())).thenReturn(userDto);
        Mockito.when(service.getRunsByUserEmail(any(String.class))).thenReturn(runDtoList);

        mockMvc.perform(get("/api/runs/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetRunById() throws Exception {
        Mockito.when(service.getRunById(anyLong())).thenReturn(runDto);

        mockMvc.perform(get("/api/runs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllRuns() throws Exception {
        Mockito.when(service.getAllRuns()).thenReturn(runDtoList);

        mockMvc.perform(get("/api/runs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAddRun() throws Exception {
        Mockito.when(service.createRun(any(RunDto.class))).thenReturn(runDto);

        String runJson = "{\"userId\":1,\"miles\":5.0,\"location\":\"INDOOR\",\"start\":\"" + LocalTime.now().toString() + "\",\"end\":\"" + LocalTime.now().plusHours(1).toString() + "\",\"date\":\"" + LocalDate.now().toString() + "\"}";

        mockMvc.perform(post("/api/runs/add")
                        .content(runJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateRun() throws Exception {
        Mockito.when(service.updateRun(any(RunDto.class))).thenReturn(runDto);

        String runJson = "{\"userId\":1,\"miles\":5.0,\"location\":\"INDOOR\",\"start\":\"" + LocalTime.now().toString() + "\",\"end\":\"" + LocalTime.now().plusHours(1).toString() + "\",\"date\":\"" + LocalDate.now().toString() + "\"}";

        mockMvc.perform(put("/api/runs/1/update")
                        .content(runJson)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteRun() throws Exception {
        mockMvc.perform(delete("/api/runs/1/delete"))
                .andExpect(status().isOk())
                .andExpect(content().string("Run deleted"));
    }
}
