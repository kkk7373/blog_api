package com.example.blog_api.controller;

import com.example.blog_api.dto.User;
import com.example.blog_api.dto.UserCreateRequest;
import com.example.blog_api.dto.UserUpdateRequest;
import com.example.blog_api.security.AuthenticationHelper;
import com.example.blog_api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final AuthenticationHelper authHelper;

    public UserController(UserService userService, AuthenticationHelper authHelper) {
        this.userService = userService;
        this.authHelper = authHelper;
    }

    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestParam("name") String name,
            @RequestParam("password") String password,
            @RequestParam("nickname") String nickname,
            @RequestParam(value = "iconFile", required = false) MultipartFile iconFile) {
        UserCreateRequest request = new UserCreateRequest();
        request.setName(name);
        request.setPassword(password);
        request.setNickname(nickname);
        
        User user = userService.createUser(request, iconFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "iconFile", required = false) MultipartFile iconFile) {
        if(!authHelper.getCurrentUserId(authHeader).equals(userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        UserUpdateRequest request = new UserUpdateRequest();
        request.setNickname(nickname);
        
        User user = userService.updateUser(userId, request, iconFile);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authHeader) {
        if(!authHelper.getCurrentUserId(authHeader).equals(userId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
