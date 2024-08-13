package com.example.demo.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.Run.Run;
import com.example.demo.entity.User;
import com.example.demo.exceptions.RunNotFoundException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.mapper.RunDto;
import com.example.demo.mapper.RunMapper;
import com.example.demo.mapper.UserDto;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RunRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RunRepository runRepository;


    public ServiceImpl(UserRepository userRepository, RunRepository runRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.runRepository = runRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //USER IMPLEMENTATION

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        if(user.getRole() == null) {
            user.setRole(Role.ROLE_GUEST);
        }
        Optional<User> check = userRepository.findByEmail(userDto.getEmail());
        if (check.isPresent()) {
            throw new IllegalArgumentException("User with email " + userDto.getEmail() + " already exists.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setVerified(true);
        User saved = userRepository.save(user);
        return UserMapper.mapToUserDto(saved);
    }

    @Override
    public UserDto getUserById(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto) throws UserNotFoundException {
        User existingUser = userRepository.findById(userDto.getId())
                .orElseThrow(UserNotFoundException::new);

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPassword(userDto.getPassword());
        existingUser.setVerified(userDto.isVerified());

        User savedUser = userRepository.save(existingUser);
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
       List<User> users = userRepository.findAll();
       return users.stream().map((user) -> UserMapper.mapToUserDto(user))
               .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

//        List<Run> runs = runRepository.findByUserId(user.getId());
//        for(Run run : runs) {
//            deleteRun(run.getId());
//        }
        userRepository.delete(user);
    }

    //RUN IMPLEMENTATION

    @Override
    @Transactional
    public RunDto createRun(RunDto runDto) throws UserNotFoundException {
        Run run = RunMapper.mapToRun(runDto);
        Long userId = runDto.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        run.setUserId(userId);
        Run savedRun = runRepository.save(run);

        user.setTotalRuns(user.getTotalRuns() + 1);
        user.setTotalDistance(user.getTotalDistance() + run.getMiles());

        userRepository.save(user);

        return RunMapper.mapToRunDto(savedRun);
    }

    @Override
    public RunDto getRunById(Long id) throws RunNotFoundException{
        Run run = runRepository.findById(id).orElseThrow(RunNotFoundException::new);
        return RunMapper.mapToRunDto(run);
    }

    @Override
    @Transactional
    public RunDto updateRun(RunDto runDto) throws RunNotFoundException, UserNotFoundException {
        Run existingRun = runRepository.findById(runDto.getId())
                .orElseThrow(RunNotFoundException::new);

        Long userId = existingRun.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        double oldMiles = existingRun.getMiles();
        double newMiles = runDto.getMiles();
        double distanceDelta = newMiles - oldMiles;

        existingRun.setMiles(newMiles);
        existingRun.setLocation(runDto.getLocation());
        existingRun.setStart(runDto.getStart());
        existingRun.setEnd(runDto.getEnd());
        existingRun.setDate(runDto.getDate());
        Run savedRun = runRepository.save(existingRun);

        if (distanceDelta != 0) {
            user.setTotalDistance(user.getTotalDistance() + distanceDelta);

            userRepository.save(user);
        }

        return RunMapper.mapToRunDto(savedRun);
    }

    @Override
    public List<RunDto> getAllRuns() {
        List<Run> runs = runRepository.findAll();
        return runs.stream().map((run) -> RunMapper.mapToRunDto(run))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteRun(Long id) throws RunNotFoundException, UserNotFoundException {
        Run run = runRepository.findById(id).orElseThrow(RunNotFoundException::new);

        Long userId = run.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        user.setTotalRuns(user.getTotalRuns() - 1);
        user.setTotalDistance(user.getTotalDistance() - run.getMiles());
        userRepository.save(user);
        runRepository.deleteById(id);
    }

    @Override
    public List<RunDto> getRunsByUserEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        List<Run> runs = runRepository.findByUserId(user.getId());
        return runs.stream().map(RunMapper::mapToRunDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
        return UserMapper.mapToUserDto(user);
    }



    //LOGIC HELP IMPLEMENTATION

    @Override
    public boolean userExists(Long userId) {
        return userRepository.existsById(userId);
    }




//    @Override
//    public UserDto updateDistance(UserDto userDto, double miles, boolean update) {
//        User user = UserMapper.mapToUser(userDto);
//        double currDistance = user.getTotalDistance();
//        if(miles > 0) {
//            user.setTotalDistance(currDistance + miles);
//        } else {
//            user.setTotalDistance(currDistance - miles);
//        }
//        if(update) {
//            int currRuns = user.getTotalRuns();
//            user.setTotalRuns(currRuns + 1);
//        }
//        User saved = userRepository.save(user);
//        return UserMapper.mapToUserDto(saved);
//    }
}
