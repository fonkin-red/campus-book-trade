package com.booktrading.book_trading_backend.controller;

import com.booktrading.book_trading_backend.dto.LoginRequest;
import com.booktrading.book_trading_backend.dto.RegisterRequest;
import com.booktrading.book_trading_backend.dto.Result;
import com.booktrading.book_trading_backend.entity.User;
import com.booktrading.book_trading_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginRequest req) {
        return Result.ok(userService.login(req));
    }

    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody RegisterRequest req) {
        userService.register(req);
        return Result.ok();
    }

    @GetMapping("/info")
    public Result<?> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(userService.getUserInfo(userId));
    }

    @PutMapping("/info")
    public Result<?> updateInfo(@RequestBody User update, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateUser(userId, update);
        return Result.ok();
    }
}
