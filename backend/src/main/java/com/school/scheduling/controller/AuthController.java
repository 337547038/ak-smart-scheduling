package com.school.scheduling.controller;

import com.school.scheduling.common.Result;
import com.school.scheduling.dto.LoginDTO;
import com.school.scheduling.dto.LoginResponse;
import com.school.scheduling.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginDTO loginDTO) {
        try {
            LoginResponse response = userService.login(loginDTO);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
