package com.booktrading.book_trading_backend.service;

import com.booktrading.book_trading_backend.dto.LoginRequest;
import com.booktrading.book_trading_backend.dto.LoginResponse;
import com.booktrading.book_trading_backend.dto.RegisterRequest;
import com.booktrading.book_trading_backend.entity.User;
import com.booktrading.book_trading_backend.mapper.UserMapper;
import com.booktrading.book_trading_backend.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest req) {
        User user = userMapper.selectByUsername(req.getUsername());
        if (user == null || user.getStatus() == 0) {
            throw new RuntimeException("用户不存在或已被禁用");
        }
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        LoginResponse.UserInfo info = new LoginResponse.UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setNickname(user.getNickname());
        info.setPhone(user.getPhone());
        info.setEmail(user.getEmail());
        info.setAvatar(user.getAvatar());
        info.setRole(user.getRole());
        resp.setUser(info);
        return resp;
    }

    public void register(RegisterRequest req) {
        User exist = userMapper.selectByUsername(req.getUsername());
        if (exist != null) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setPhone(req.getPhone());
        user.setRole(0);
        user.setStatus(1);
        userMapper.insert(user);
    }

    public LoginResponse.UserInfo getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        LoginResponse.UserInfo info = new LoginResponse.UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setNickname(user.getNickname());
        info.setPhone(user.getPhone());
        info.setEmail(user.getEmail());
        info.setAvatar(user.getAvatar());
        info.setRole(user.getRole());
        return info;
    }

    public void updateUser(Long userId, User update) {
        update.setId(userId);
        userMapper.updateProfile(update);
    }
}
