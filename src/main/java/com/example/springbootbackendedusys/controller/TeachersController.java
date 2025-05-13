package com.example.springbootbackendedusys.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.springbootbackendedusys.entity.Teachers;
import com.example.springbootbackendedusys.service.ITeachersService;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.springbootbackendedusys.common.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    Teachers teacher = teachersService.getById(id);
    return teacher != null ? R.ok().data(teacher) : R.error().message("教师不存在");
  }

  @PostMapping
  public R createTeacher(@RequestBody Teachers teacher) {
    boolean success = teachersService.save(teacher);
    return success ? R.ok().message("教师创建成功") : R.error().message("教师创建失败");
  }

  @PutMapping("/{id}")
  public R updateTeacher(@PathVariable Integer id, @RequestBody Teachers teacher) {
    teacher.setId(id);
    boolean success = teachersService.updateById(teacher);
    return success ? R.ok().message("教师更新成功") : R.error().message("教师更新失败");
  }

  @DeleteMapping("/{id}")
  public R deleteTeacher(@PathVariable Integer id) {
    boolean success = teachersService.removeById(id);
    return success ? R.ok().message("教师删除成功") : R.error().message("教师删除失败");
  }

  @PostMapping("/login")
  public R login(@RequestBody Teachers teacher) {
    Teachers result = teachersService.lambdaQuery()
      .eq(Teachers::getUsername, teacher.getUsername())
      .eq(Teachers::getPassword, teacher.getPassword())
      .one();
    return result != null ? R.ok().data(result) : R.error().message("用户名或密码错误");
  }
}
