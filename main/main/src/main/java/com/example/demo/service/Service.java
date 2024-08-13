package com.example.demo.service;

import com.example.demo.exceptions.RunNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.mapper.RunDto;
import com.example.demo.mapper.UserDto;

import java.util.List;

@org.springframework.stereotype.Service
public interface Service {

    UserDto createUser(UserDto userDto);
    UserDto getUserById(Long id) throws UserNotFoundException;
    UserDto updateUser(UserDto userDto) throws UserNotFoundException;
    List<UserDto> getAllUsers();
    void deleteUser(Long id) throws UserNotFoundException;

    RunDto createRun(RunDto runDto) throws UserNotFoundException;
    RunDto getRunById(Long id) throws RunNotFoundException;
    RunDto updateRun(RunDto runDto) throws RunNotFoundException, UserNotFoundException;
    List<RunDto> getAllRuns();
    void deleteRun(Long id) throws RunNotFoundException, UserNotFoundException;

    boolean userExists(Long userId);

    List<RunDto> getRunsByUserEmail(String name);

    UserDto getUserByEmail(String name);
//    UserDto updateDistance(UserDto userDto, double miles, boolean update);
}
