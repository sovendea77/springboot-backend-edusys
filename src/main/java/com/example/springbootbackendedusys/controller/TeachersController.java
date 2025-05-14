package com.example.springbootbackendedusys.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.springbootbackendedusys.entity.Teachers;
import com.example.springbootbackendedusys.service.ITeachersService;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.springbootbackendedusys.common.R;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author sovendea
 * @since 2025-05-12
 */
@RestController
@RequestMapping("/api/teachers")
public class TeachersController {

  @Autowired
  private ITeachersService teachersService;

  @GetMapping
  public R getAllTeachers() {
    List<Teachers> teachers = teachersService.list();
    return R.ok().data(teachers);
  }

  @GetMapping("/{id}")
  public R getTeacherById(@PathVariable Integer id) {
    try {
      Teachers teacher = teachersService.getById(id);
      if (teacher == null) {
        return R.error().message("教师不存在");
      }
      return R.ok().data(teacher);
    } catch (Exception e) {
      return R.error().message("获取教师信息失败: " + e.getMessage());
    }
  }

  @PostMapping
  public R createTeacher(@RequestBody Teachers teacher) {
    try {
      // 检查用户名是否已存在
      Teachers existingTeacher = teachersService.lambdaQuery()
        .eq(Teachers::getUsername, teacher.getUsername())
        .one();

      if (existingTeacher != null) {
        return R.error().message("用户名已存在");
      }

      boolean success = teachersService.save(teacher);
      if (!success) {
        return R.error().message("创建教师失败");
      }

      // 不返回密码
      teacher.setPassword(null);
      return R.ok().message("教师创建成功").data(teacher);
    } catch (Exception e) {
      return R.error().message("创建教师失败: " + e.getMessage());
    }
  }

  @PutMapping("/{id}")
  public R updateTeacher(@PathVariable Integer id, @RequestBody Teachers teacher) {
    try {
      // 检查教师是否存在
      Teachers existingTeacher = teachersService.getById(id);
      if (existingTeacher == null) {
        return R.error().message("教师不存在");
      }

      // 如果更改了用户名，检查新用户名是否已存在
      if (!existingTeacher.getUsername().equals(teacher.getUsername())) {
        Teachers teacherWithNewUsername = teachersService.lambdaQuery()
          .eq(Teachers::getUsername, teacher.getUsername())
          .one();
        if (teacherWithNewUsername != null && !teacherWithNewUsername.getId().equals(id)) {
          return R.error().message("用户名已存在");
        }
      }

      teacher.setId(id);
      boolean success = teachersService.updateById(teacher);
      if (!success) {
        return R.error().message("更新教师失败");
      }

      // 不返回密码
      teacher.setPassword(null);
      return R.ok().message("教师更新成功").data(teacher);
    } catch (Exception e) {
      return R.error().message("更新教师失败: " + e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public R deleteTeacher(@PathVariable Integer id) {
    try {
      Teachers teacher = teachersService.getById(id);
      if (teacher == null) {
        return R.error().message("教师不存在");
      }

      boolean success = teachersService.removeById(id);
      return success ? R.ok().message("教师删除成功") : R.error().message("教师删除失败");
    } catch (Exception e) {
      return R.error().message("删除教师失败: " + e.getMessage());
    }
  }

  @PostMapping("/login")
  public R login(@RequestBody Teachers teacher) {
    try {
      Teachers result = teachersService.lambdaQuery()
        .eq(Teachers::getUsername, teacher.getUsername())
        .eq(Teachers::getPassword, teacher.getPassword())
        .one();

      if (result == null) {
        return R.error().message("教师账号或密码错误");
      }

      // 创建返回的教师数据（不包含密码）
      Map<String, Object> teacherData = new HashMap<>();
      teacherData.put("id", result.getId());
      teacherData.put("username", result.getUsername());
      teacherData.put("name", result.getName());
      teacherData.put("email", result.getEmail());

      return R.ok().data(teacherData);
    } catch (Exception e) {
      return R.error().message("教师登录失败: " + e.getMessage());
    }
  }
}
