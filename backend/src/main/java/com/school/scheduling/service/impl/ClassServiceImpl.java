package com.school.scheduling.service.impl;

import com.school.scheduling.entity.ClassInfo;
import com.school.scheduling.mapper.ClassMapper;
import com.school.scheduling.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Override
    public List<ClassInfo> getAllClasses() {
        return classMapper.findAll();
    }

    @Override
    public ClassInfo getById(Long id) {
        return classMapper.findById(id);
    }

    @Override
    public boolean addClass(ClassInfo classInfo) {
        return classMapper.insert(classInfo) > 0;
    }

    @Override
    public boolean updateClass(ClassInfo classInfo) {
        return classMapper.update(classInfo) > 0;
    }

    @Override
    public boolean deleteClass(Long id) {
        return classMapper.deleteById(id) > 0;
    }
}
