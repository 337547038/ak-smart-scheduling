package com.school.scheduling.service;

import com.school.scheduling.dto.LoginDTO;
import com.school.scheduling.dto.LoginResponse;
import com.school.scheduling.entity.User;
import java.util.List;

public interface UserService {
    LoginResponse login(LoginDTO loginDTO);
    List<User> getAllTeachers();
    List<User> getAllUsers();
    User getById(Long id);
    boolean addTeacher(User user);
    boolean updateUser(User user);
    boolean deleteTeacher(Long id);
}
