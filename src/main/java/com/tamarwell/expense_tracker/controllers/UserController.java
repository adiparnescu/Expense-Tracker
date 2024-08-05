package com.tamarwell.expense_tracker.controllers;

import com.tamarwell.expense_tracker.dto.UserDto;
import com.tamarwell.expense_tracker.entity.User;
import com.tamarwell.expense_tracker.exception.EmailAlreadyRegisteredException;
import com.tamarwell.expense_tracker.exception.UsernameAlreadyTakenException;
import com.tamarwell.expense_tracker.exception.WeakPasswordException;
import com.tamarwell.expense_tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/roles")
    public ResponseEntity<?> getAuthenticatedUserRole(){
        Long authenticatedUserId = getAuthenticatedUserId();
        List<String> roles = userService.getUserRolesById(authenticatedUserId);
        if (roles.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User has no roles assigned");
            return ResponseEntity.status(404).body(response); // Return 404 Not Found with a message
        }
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody UserDto user) {
        try {
            User registeredUser = userService.registerUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (UsernameAlreadyTakenException | EmailAlreadyRegisteredException | WeakPasswordException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            if (userDetails.getUsername().isEmpty())
                throw new UsernameNotFoundException("Authenticated user not found");
            else
                return userService.findByUsername(userDetails.getUsername()).get().getId();
        }
        return null;
    }
}
