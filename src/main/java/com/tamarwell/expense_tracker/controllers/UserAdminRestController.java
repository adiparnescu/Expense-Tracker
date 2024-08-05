package com.tamarwell.expense_tracker.controllers;

import com.tamarwell.expense_tracker.dto.TransactionDto;
import com.tamarwell.expense_tracker.dto.UserDto;
import com.tamarwell.expense_tracker.entity.User;
import com.tamarwell.expense_tracker.exception.*;
import com.tamarwell.expense_tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class UserAdminRestController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<?> getAllUsers(){
        List<User> allUsers = userService.getAllUsers();
        if(allUsers.isEmpty()){
            Map<String, String> response = new HashMap<>();
            response.put("message", "No users registered");
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(allUsers);
    }

    @DeleteMapping("/")
    public ResponseEntity<?> deleteAllUsers()
    {
        userService.deleteAllUsers();
        return createResponseEntity(HttpStatus.OK, "response", "All users deleted!");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestBody @Valid UserDto userDto, @PathVariable Long id){
        try{
            User updatedUser = userService.updateUser(userDto, id);
            return ResponseEntity.ok(updatedUser);
        }catch (UsernameAlreadyTakenException | EmailAlreadyRegisteredException | WeakPasswordException |
                UserNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUserById(@PathVariable Long id) {
        try {
            userService.deleteUserById(id);
            return createResponseEntity(HttpStatus.OK, "response", "User deleted!");
        } catch (UserNotFoundException e) {
            return createResponseEntity(HttpStatus.BAD_REQUEST, "error", e.getMessage());
        } catch (Exception e) {
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "error", "An unexpected error occurred");
        }
    }

    @GetMapping("/username")
    public ResponseEntity<?> getUserByUsername(@RequestParam String username){
        Optional<User> returnedUser = userService.findByUsername(username);
        if(returnedUser.isEmpty()){
            Map<String, String> response = new HashMap<>();
            response.put("message", "No user found with username: " + username);
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(returnedUser);
    }

    @DeleteMapping("/username")
    public ResponseEntity<Map<String, String>> deleteUserByUsername(@RequestParam String username) {
        try {
            userService.deleteUserByUsername(username);
            return createResponseEntity(HttpStatus.OK, "response", "User deleted!");
        } catch (UserNotFoundException e) {
            return createResponseEntity(HttpStatus.BAD_REQUEST, "error", e.getMessage());
        } catch (Exception e) {
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "error", "An unexpected error occurred");
        }
    }

    @GetMapping("/email")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email){
        Optional<User> returnedUser = userService.findByEmail(email);
        if(returnedUser.isEmpty()){
            Map<String, String> response = new HashMap<>();
            response.put("message", "No user found with email: " + email);
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(returnedUser);
    }

    @DeleteMapping("/email")
    public ResponseEntity<Map<String, String>> deleteUserByEmail(@RequestParam String email) {
        try {
            userService.deleteUserByEmail(email);
            return createResponseEntity(HttpStatus.OK, "response", "User deleted!");
        } catch (UserNotFoundException e) {
            return createResponseEntity(HttpStatus.BAD_REQUEST, "error", e.getMessage());
        } catch (Exception e) {
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "error", "An unexpected error occurred");
        }
    }

    @PutMapping("/role")
    public ResponseEntity<?> makeAdmin(@RequestParam Long id){
        try{
            User adminUser = userService.makeUserToAdmin(id);
            return ResponseEntity.ok(adminUser);
        }catch (UserNotFoundException | UserAlreadyAdminException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    private ResponseEntity<Map<String, String>> createResponseEntity(HttpStatus status, String key, String message) {
        Map<String, String> response = new HashMap<>();
        response.put(key, message);
        return new ResponseEntity<>(response, status);
    }
}
