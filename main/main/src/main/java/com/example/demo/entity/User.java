package com.example.demo.entity;

import com.example.demo.entity.Run.Run;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "Users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private int totalRuns = 0;
    private double totalDistance = 0;
    @Enumerated(EnumType.STRING)
    private Role role;
    boolean verified = false;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private List<Run> runs = new ArrayList<>();

    // Existing constructor
    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.totalRuns = 0;
        this.totalDistance = 0.0;
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = Role.ROLE_GUEST;
        this.totalRuns = 0;
        this.totalDistance = 0.0;
    }

    // Add this constructor if needed for creating user with just email and password
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email; // Use email as the username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Assuming account is never expired, adjust as necessary
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Assuming account is never locked, adjust as necessary
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Assuming credentials are never expired, adjust as necessary
    }

    @Override
    public boolean isEnabled() {
        return true; // Assuming account is always enabled, adjust as necessary
    }

    public boolean getVerified(){
        return this.verified;
    }
}
