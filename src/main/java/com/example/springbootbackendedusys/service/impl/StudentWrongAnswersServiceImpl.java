package com.example.springbootbackendedusys.service.impl;

import com.example.springbootbackendedusys.entity.StudentWrongAnswers;
import com.example.springbootbackendedusys.mapper.StudentWrongAnswersMapper;
import com.example.springbootbackendedusys.service.IStudentWrongAnswersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.example.springbootbackendedusys.entity.StudentGrade;
import com.example.springbootbackendedusys.mapper.StudentGradeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;



/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sovendea
 * @since 2025-05-12
 */
@Service
public class StudentWrongAnswersServiceImpl extends ServiceImpl<StudentWrongAnswersMapper, StudentWrongAnswers> implements IStudentWrongAnswersService {

    @Autowired
    private StudentGradeMapper studentGradeMapper;

    @Override
    public List<Map<String, Object>> getStudentWrongAnswers(Integer examId, String studentId) {
        return baseMapper.selectWrongAnswersWithQuestionInfo(examId, studentId);
    }

    @Override
    public List<Map<String, Object>> getWrongAnswersAnalysis(Integer examId, Integer minErrorCount) {
        return baseMapper.selectWrongAnswersAnalysis(examId, minErrorCount);
    }

    @Override
    @Transactional
    public boolean updateEssayQuestionScore(Integer examId, Long studentId, Integer questionId, Double score) {
        return baseMapper.updateEssayQuestionScore(examId, studentId, questionId, score) > 0;
    }

    @Override
    @Transactional
    public boolean updateFillQuestionCorrectness(Integer examId, Long studentId, Integer questionId, Boolean isCorrect) {
        return baseMapper.updateFillQuestionCorrectness(examId, studentId, questionId, isCorrect) > 0;
    }

    @Override
    @Transactional
    public boolean saveStudentAnswersWithGrades(Integer examId, Long studentId, String studentName,
                                              Double totalScore, List<Map<String, Object>> answers) {
        try {
          // 1. 保存学生成绩
          StudentGrade grade = new StudentGrade();
          grade.setExamId(examId);
          grade.setId(studentId);
          grade.setStudentName(studentName);
          grade.setScore(totalScore);
          studentGradeMapper.insert(grade);

          // 2. 保存学生答案和错题记录
          for (Map<String, Object> answer : answers) {
            StudentWrongAnswers wrongAnswer = new StudentWrongAnswers();
            wrongAnswer.setExamId(examId);
            wrongAnswer.setStudentId(studentName);
            wrongAnswer.setQuestionId(Integer.valueOf(answer.get("questionId").toString()));
            wrongAnswer.setStudentAnswer(answer.get("studentAnswer").toString());
            wrongAnswer.setIsCorrected((Boolean) answer.get("isCorrect"));
            wrongAnswer.setScore(Double.valueOf(answer.get("score").toString()));
            baseMapper.insert(wrongAnswer);
          }

          return true;
        } catch (Exception e) {
          throw new RuntimeException("保存学生答案和成绩失败: " + e.getMessage());
        }
    }
}
