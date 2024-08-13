package com.example.demo.mapper;

import com.example.demo.entity.User;

public class UserMapper {

    // Mapping UserDto to User without setting internal fields
    public static User mapToUser(UserDto userDto) {
        User user = new User(
                userDto.getName(),
                userDto.getEmail(),
                userDto.getPassword(),
                userDto.getRole()
                // Do not set totalRuns and totalDistance here
        );
        return user;
    }

    // Mapping User to UserDto including internal fields
    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setTotalRuns(user.getTotalRuns());
        userDto.setTotalDistance(user.getTotalDistance());
        userDto.setRole(user.getRole());
        userDto.setVerified(user.getVerified());
        userDto.setRuns(user.getRuns());
        return userDto;
    }
}