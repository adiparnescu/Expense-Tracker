package com.tamarwell.expense_tracker.service;


import com.tamarwell.expense_tracker.dto.UserDto;
import com.tamarwell.expense_tracker.entity.Role;
import com.tamarwell.expense_tracker.entity.Transaction;
import com.tamarwell.expense_tracker.entity.User;
import com.tamarwell.expense_tracker.exception.*;
import com.tamarwell.expense_tracker.repository.RoleRepository;
import com.tamarwell.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public List<String> getUserRolesById(Long userId) { return userRepository.findRoleNamesById(userId);
    }

    public User registerUser(UserDto registrationUser) {
        User registeredUser = convertUserDto(registrationUser);

        if (userRepository.findByUsername(registeredUser.getUsername()).isPresent()) {
            throw new UsernameAlreadyTakenException("Username already taken");
        }

        if (userRepository.findByEmail(registeredUser.getEmail()).isPresent()) {
            throw new EmailAlreadyRegisteredException("Email already registered");
        }

        if (registrationUser.getPassword().length() < 6) {
            throw new WeakPasswordException("Password should be at least 6 characters long");
        }
        System.out.println(registeredUser);
        return userRepository.save(registeredUser);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    public User updateUser(UserDto userDto, Long id) {
        Optional <User> oldUserOpt = userRepository.findById(id);
        if(oldUserOpt.isEmpty()){
            throw new UserNotFoundException("User not found!");
        }

        User updatedUser = convertUserDto(userDto);
        User oldUserObj = oldUserOpt.get();

        if (userRepository.findByUsername(updatedUser.getUsername()).isPresent() &&
                !Objects.equals(userRepository.findByUsername(updatedUser.getUsername()).get().getUsername(), oldUserObj.getUsername())) {
            throw new UsernameAlreadyTakenException("Username already taken");
        }

        if (userRepository.findByEmail(updatedUser.getEmail()).isPresent() &&
                !Objects.equals(userRepository.findByEmail(updatedUser.getEmail()).get().getEmail(), oldUserObj.getEmail())) {
            throw new EmailAlreadyRegisteredException("Email already registered");
        }

        if (updatedUser.getPassword().length() < 6) {
            throw new WeakPasswordException("Password should be at least 6 characters long");
        }


        oldUserObj.setFirstName(updatedUser.getFirstName());
        oldUserObj.setLastName(updatedUser.getLastName());
        oldUserObj.setEmail(updatedUser.getEmail());
        oldUserObj.setUsername(updatedUser.getUsername());
        oldUserObj.setUpdatedAt(updatedUser.getUpdatedAt());

        if(!Objects.equals(userDto.getPassword(), "default")) {
            oldUserObj.setPassword(updatedUser.getPassword());
        }

        return userRepository.save(oldUserObj);
    }

    @Transactional
    public User makeUserToAdmin(Long id) {

        Optional<User> normalUser = userRepository.findById(id);

        if(normalUser.isEmpty()){
            throw new UserNotFoundException("User not found!");
        }

        Set<Role> roles = userRepository.findRolesByUserId(id);
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");

        if(roles.contains(adminRole)){
            throw new UserAlreadyAdminException("User is already an admin!");
        }

        roles.add(adminRole);
        normalUser.get().setRoles(roles);

        return userRepository.save(normalUser.get());
    }

    @Transactional
    public void deleteUserById(Long id){
        Optional<User> userToDelete = userRepository.findById(id);
        if(userToDelete.isEmpty()){
            throw new UserNotFoundException("User not found!");
        }

        userRepository.deleteUserById(id);
    }

    @Transactional
    public void deleteUserByUsername(String username){
        Optional<User> userToDelete = userRepository.findByUsername(username);
        if(userToDelete.isEmpty()){
            throw new UserNotFoundException("User not found!");
        }

        userRepository.deleteUserByUsername(username);
    }

    @Transactional
    public void deleteUserByEmail(String email){
        Optional<User> userToDelete = userRepository.findByEmail(email);
        if(userToDelete.isEmpty()){
            throw new UserNotFoundException("User not found!");
        }

        userRepository.deleteUserByEmail(email);
    }

    @Transactional
    public void deleteAllUsers(){
        userRepository.deleteAll();
    }


    private User convertUserDto(UserDto userDto){
        User convertedUser = new User();

        convertedUser.setUsername(userDto.getUsername());
        convertedUser.setEmail(userDto.getEmail());
        convertedUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        convertedUser.setFirstName(userDto.getFirstName());
        convertedUser.setLastName(userDto.getLastName());
        convertedUser.setCreatedAt(Instant.now());
        convertedUser.setUpdatedAt(Instant.now());

        Role userRole = roleRepository.findByName("ROLE_USER");

        if (userRole == null) {
            throw new RuntimeException("Default role not found");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        convertedUser.setRoles(roles);

        return convertedUser;
    }
}
