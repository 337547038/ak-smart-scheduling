package com.school.scheduling.service;

import com.school.scheduling.entity.ClassInfo;
import java.util.List;

public interface ClassService {
    List<ClassInfo> getAllClasses();
    ClassInfo getById(Long id);
    boolean addClass(ClassInfo classInfo);
    boolean updateClass(ClassInfo classInfo);
    boolean deleteClass(Long id);
}
