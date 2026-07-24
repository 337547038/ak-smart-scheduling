package com.school.scheduling.mapper;

import com.school.scheduling.entity.ClassInfo;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface ClassMapper {
    List<ClassInfo> findAll();
    ClassInfo findById(@Param("id") Long id);
    int insert(ClassInfo classInfo);
    int update(ClassInfo classInfo);
    int deleteById(@Param("id") Long id);
}
