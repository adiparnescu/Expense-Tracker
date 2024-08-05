package com.tamarwell.expense_tracker.controllers;

import com.tamarwell.expense_tracker.dto.UserDto;
import com.tamarwell.expense_tracker.entity.User;
import com.tamarwell.expense_tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
public class HomeController {

    @GetMapping("/home")
    public String handleWelcome() {
        return "home";
    }

    @GetMapping("/login")
    public String handleLogin() {
        return "login";
    }

    @GetMapping("/register")
    public String handleRegister() {
        return "register";
    }

    @GetMapping("/home/user")
    public String handleUserHome() {
        return "userHome";
    }

    @GetMapping("/home/admin")
    public String handleAdminHome() {
        return "adminHome";
    }
}
