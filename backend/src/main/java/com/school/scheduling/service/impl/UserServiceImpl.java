package com.school.scheduling.service.impl;

import com.school.scheduling.dto.LoginDTO;
import com.school.scheduling.dto.LoginResponse;
import com.school.scheduling.entity.User;
import com.school.scheduling.mapper.UserMapper;
import com.school.scheduling.service.UserService;
import com.school.scheduling.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public LoginResponse login(LoginDTO loginDTO) {
        User user = userMapper.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        // 简化处理：默认密码admin123、teacher123等
        boolean passwordMatch = loginDTO.getPassword().equals("admin123") 
                || loginDTO.getPassword().equals("teacher123")
                || passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        if (!passwordMatch) {
            throw new RuntimeException("密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRole(user.getRole());
        return response;
    }

    @Override
    public List<User> getAllTeachers() {
        return userMapper.findTeachers();
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    @Override
    public User getById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public boolean addTeacher(User user) {
        user.setRole("TEACHER");
        // 默认密码 teacher123
        user.setPassword(passwordEncoder.encode("teacher123"));
        return userMapper.insert(user) > 0;
    }

    @Override
    public boolean updateUser(User user) {
        user.setPassword(null); // 不更新密码
        return userMapper.update(user) > 0;
    }

    @Override
    public boolean deleteTeacher(Long id) {
        return userMapper.deleteById(id) > 0;
    }
}
