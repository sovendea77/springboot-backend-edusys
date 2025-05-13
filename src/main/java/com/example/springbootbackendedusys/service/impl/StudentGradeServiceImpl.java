package com.example.springbootbackendedusys.service.impl;

import com.example.springbootbackendedusys.entity.StudentGrade;
import com.example.springbootbackendedusys.mapper.StudentGradeMapper;
import com.example.springbootbackendedusys.service.IStudentGradeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springbootbackendedusys.entity.Exams;
import com.example.springbootbackendedusys.entity.QuestionAnswers;
import com.example.springbootbackendedusys.entity.StudentGrade;
import com.example.springbootbackendedusys.entity.StudentWrongAnswers;
import com.example.springbootbackendedusys.mapper.StudentGradeMapper;
import com.example.springbootbackendedusys.service.IExamsService;
import com.example.springbootbackendedusys.service.IQuestionAnswersService;
import com.example.springbootbackendedusys.service.IStudentGradeService;
import com.example.springbootbackendedusys.service.IStudentWrongAnswersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sovendea
 * @since 2025-05-12
 */
@Service
public class StudentGradeServiceImpl extends ServiceImpl<StudentGradeMapper, StudentGrade> implements IStudentGradeService {

  @Autowired
  private IExamsService examsService;

  @Autowired
  private IQuestionAnswersService questionAnswersService;

  @Autowired
  private IStudentWrongAnswersService studentWrongAnswersService;

}

