package com.example.demo.mapper;

import com.example.demo.entity.Role;
import com.example.demo.entity.Run.Run;
import com.example.demo.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private int totalRuns;
    private double totalDistance;
    private Role role;
    private boolean verified;
    private List<Run> runs;

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole();
    }


}
