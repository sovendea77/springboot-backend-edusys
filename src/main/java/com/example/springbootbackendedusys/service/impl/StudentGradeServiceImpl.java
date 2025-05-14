package com.example.springbootbackendedusys.service.impl;

import com.example.springbootbackendedusys.entity.QuestionAnswers;
import com.example.springbootbackendedusys.entity.StudentGrade;
import com.example.springbootbackendedusys.mapper.StudentGradeMapper;
import com.example.springbootbackendedusys.service.IStudentGradeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springbootbackendedusys.entity.StudentWrongAnswers;
import com.example.springbootbackendedusys.service.IStudentWrongAnswersService;
import com.example.springbootbackendedusys.service.IQuestionAnswersService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private IStudentWrongAnswersService studentWrongAnswersService;

    @Autowired
    private IQuestionAnswersService questionAnswersService;

    @Override
    @Transactional
    public int calculateAndSaveGrades(Integer examId) {
        // 获取所有学生的答案记录
        List<StudentWrongAnswers> studentAnswers = studentWrongAnswersService.list(
            new LambdaQueryWrapper<StudentWrongAnswers>()
                .eq(StudentWrongAnswers::getExamId, examId)
        );

        // 获取所有题目信息
        Map<Integer, QuestionAnswers> questionMap = questionAnswersService.list(
            new LambdaQueryWrapper<QuestionAnswers>()
                .eq(QuestionAnswers::getExamId, examId)
        ).stream().collect(Collectors.toMap(QuestionAnswers::getId, q -> q));

        // 按学生ID分组计算成绩
        Map<String, Double> studentScores = new HashMap<>();

        // 按学生ID和题目ID分组，确保每道题只计算一次
        Map<String, Map<Integer, StudentWrongAnswers>> studentAnswersMap = studentAnswers.stream()
            .collect(Collectors.groupingBy(StudentWrongAnswers::getStudentId,
                Collectors.toMap(StudentWrongAnswers::getQuestionId, answer -> answer,
                    (existing, replacement) -> replacement)));  // 如果有重复，使用最新的记录

        // 计算每个学生的总分
        for (Map.Entry<String, Map<Integer, StudentWrongAnswers>> studentEntry : studentAnswersMap.entrySet()) {
            String studentId = studentEntry.getKey();
            double totalScore = 0.0;

            for (StudentWrongAnswers answer : studentEntry.getValue().values()) {
                QuestionAnswers questionInfo = questionMap.get(answer.getQuestionId());
                if (questionInfo != null) {
                    String sectionType = questionInfo.getSectionType();
                    
                    if ("essay".equals(sectionType)) {
                        // 主观题使用批改的分数
                        totalScore += (answer.getScore() != null ? answer.getScore() : 0.0);
                    } else {
                        // 客观题根据是否正确给分
                        totalScore += answer.getIsCorrected() ?
                            (questionInfo.getScore() != null ? questionInfo.getScore().doubleValue() : 0.0) : 0.0;
                    }
                }
            }
            
            studentScores.put(studentId, totalScore);
        }

        // 删除原有成绩记录
        remove(new LambdaQueryWrapper<StudentGrade>()
            .eq(StudentGrade::getExamId, examId));

        // 保存新的成绩记录
        List<StudentGrade> gradesToSave = studentScores.entrySet().stream()
            .map(entry -> {
                StudentGrade grade = new StudentGrade();
                grade.setExamId(examId);
                grade.setStudentName(entry.getKey());
                grade.setScore(entry.getValue());
                return grade;
            })
            .collect(Collectors.toList());

        if (!gradesToSave.isEmpty()) {
            saveBatch(gradesToSave);
        }

        return gradesToSave.size();
    }
}

