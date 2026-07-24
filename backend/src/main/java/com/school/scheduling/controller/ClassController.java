package com.school.scheduling.controller;

import com.school.scheduling.common.Result;
import com.school.scheduling.entity.ClassInfo;
import com.school.scheduling.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class")
@CrossOrigin
public class ClassController {

    @Autowired
    private ClassService classService;

    @GetMapping("/list")
    public Result<List<ClassInfo>> list() {
        return Result.success(classService.getAllClasses());
    }

    @GetMapping("/{id}")
    public Result<ClassInfo> getById(@PathVariable Long id) {
        return Result.success(classService.getById(id));
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody ClassInfo classInfo) {
        try {
            boolean success = classService.addClass(classInfo);
            return success ? Result.success() : Result.error("添加失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody ClassInfo classInfo) {
        try {
            boolean success = classService.updateClass(classInfo);
            return success ? Result.success() : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            boolean success = classService.deleteClass(id);
            return success ? Result.success() : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
