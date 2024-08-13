//package com.example.demo.controller;
//
//import com.example.demo.entity.Run.Run;
//import com.example.demo.entity.User;
//import com.example.demo.exceptions.UserNotFoundException;
//import com.example.demo.mapper.RunDto;
//import com.example.demo.mapper.UserDto;
//import com.example.demo.repository.RunRepository;
//import com.example.demo.repository.UserRepository;
//import com.example.demo.service.Service;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.*;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/users")
//public class UserController {
//
//    private final Service service;
//    private final RunRepository runRepository;
//    private final UserRepository userRepository;
//
//    public UserController(Service service, RunRepository runRepository, UserRepository userRepository) {
//        this.service = service;
//        this.runRepository = runRepository;
//        this.userRepository = userRepository;
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
//        UserDto userDto = service.getUserById(id);
//
//        BigDecimal roundedTotalDistance = BigDecimal.valueOf(userDto.getTotalDistance())
//                .setScale(2, RoundingMode.HALF_UP);
//        userDto.setTotalDistance(roundedTotalDistance.doubleValue());
//
//        return ResponseEntity.ok(userDto);
//    }
//
//    @GetMapping()
//    public ResponseEntity<List<UserDto>> getAllUsers() {
//        List<UserDto> users = service.getAllUsers();
//
//        users.forEach(userDto -> {
//            BigDecimal roundedTotalDistance = BigDecimal.valueOf(userDto.getTotalDistance())
//                    .setScale(2, RoundingMode.HALF_UP);
//            userDto.setTotalDistance(roundedTotalDistance.doubleValue());
//        });
//
//        return ResponseEntity.ok(users);
//    }
//
//    //only users with role user can access this endpoint
//    @PreAuthorize("hasRole('USER')")
//    @GetMapping("/user")
//    public String userEndpoint(){
//        return "Hello, user!";
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/admin")
//    public String adminEndpoint(){
//        return "Hello, admin!";
//    }
//
//    //only users with role admin can run this
//    @PreAuthorize("hasRole('ADMIN')")
//    @PostMapping("/add")
//    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto) {
//        return new ResponseEntity<>(service.createUser(userDto), HttpStatus.CREATED);
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @PutMapping("/{id}/update")
//    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
//
//        userDto.setId(id);
//        return new ResponseEntity<>(service.updateUser(userDto), HttpStatus.OK);
//    }
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/{id}/delete")
//    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws UserNotFoundException{
//
//        User user = userRepository.findById(id)
//                .orElseThrow(UserNotFoundException::new);
//
//        List<Run> runs = runRepository.findByUserId(id);
//        for(Run run : runs) {
//            service.deleteRun(run.getId());
//        }
//        service.deleteUser(id);
//        return ResponseEntity.ok("user deleted");
//    }
//
//}
//
package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserDto;
import com.example.demo.service.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private final Service service;
    private static final Logger logger = Logger.getLogger(UserController.class.getName());


    public UserController(Service service) {
        this.service = service;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(Principal principal) {
        UserDto userDto = service.getUserByEmail(principal.getName());

        logger.info("Logged in User: " + userDto.getEmail());
        logger.info("User Role: " + userDto.getRole());

        BigDecimal roundedTotalDistance = BigDecimal.valueOf(userDto.getTotalDistance())
                .setScale(2, RoundingMode.HALF_UP);
        userDto.setTotalDistance(roundedTotalDistance.doubleValue());
        return ResponseEntity.ok(userDto);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto userDto = service.getUserById(id);
        BigDecimal roundedTotalDistance = BigDecimal.valueOf(userDto.getTotalDistance())
                .setScale(2, RoundingMode.HALF_UP);
        userDto.setTotalDistance(roundedTotalDistance.doubleValue());
        return ResponseEntity.ok(userDto);
    }

    //can work for someone with role "ROLE_USER" for reference
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/test")
    public ResponseEntity<String> testUserRole() {
        return ResponseEntity.ok("Access granted for USER role");
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = service.getAllUsers();
        users.forEach(userDto -> {
            BigDecimal roundedTotalDistance = BigDecimal.valueOf(userDto.getTotalDistance())
                    .setScale(2, RoundingMode.HALF_UP);
            userDto.setTotalDistance(roundedTotalDistance.doubleValue());
        });
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(service.createUser(userDto), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/update")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        return new ResponseEntity<>(service.updateUser(userDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        service.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }
}
