package com.example.springbootbackendedusys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.springbootbackendedusys.entity.QuestionAnswers;
import com.example.springbootbackendedusys.entity.StudentGrade;
import com.example.springbootbackendedusys.entity.StudentWrongAnswers;
import com.example.springbootbackendedusys.mapper.StudentGradeMapper;
import com.example.springbootbackendedusys.mapper.StudentWrongAnswersMapper;
import com.example.springbootbackendedusys.service.IQuestionAnswersService;
import com.example.springbootbackendedusys.service.IStudentWrongAnswersService;
import com.example.springbootbackendedusys.service.IStudentGradeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudentWrongAnswersServiceImpl extends ServiceImpl<StudentWrongAnswersMapper, StudentWrongAnswers> implements IStudentWrongAnswersService {

    @Autowired
    private StudentGradeMapper studentGradeMapper;

    @Autowired
    private IQuestionAnswersService questionAnswersService;

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
    public boolean saveStudentAnswersWithGrades(Integer examId, String studentName,
                                              Double totalScore, List<Map<String, Object>> answers) {
        try {
            // 1. 保存学生成绩
            StudentGrade grade = new StudentGrade();
            grade.setExamId(examId);
            grade.setStudentName(studentName);
            grade.setScore(totalScore);
            studentGradeMapper.insert(grade);

            // 2. 保存学生答案和错题记录
            for (Map<String, Object> answer : answers) {
                // 获取题目信息
                QuestionAnswers questionInfo = questionAnswersService.lambdaQuery()
                    .eq(QuestionAnswers::getExamId, examId)
                    .eq(QuestionAnswers::getChineseNumber, answer.get("chineseNumber"))
                    .eq(QuestionAnswers::getQuestionNumber, answer.get("questionNumber"))
                    .one();

                if (questionInfo == null) {
                    throw new RuntimeException("未找到对应的题目信息: 题号 " + answer.get("questionNumber"));
                }

                // 删除已存在的记录
                LambdaQueryWrapper<StudentWrongAnswers> deleteWrapper = new LambdaQueryWrapper<>();
                deleteWrapper.eq(StudentWrongAnswers::getExamId, examId)
                           .eq(StudentWrongAnswers::getStudentId, studentName)
                           .eq(StudentWrongAnswers::getQuestionId, questionInfo.getId());
                baseMapper.delete(deleteWrapper);

                // 插入新记录
                StudentWrongAnswers wrongAnswer = new StudentWrongAnswers();
                wrongAnswer.setExamId(examId);
                wrongAnswer.setStudentId(studentName);
                wrongAnswer.setQuestionId(questionInfo.getId());
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

  @Override
  @Transactional
  public boolean updateEssayQuestionScore(Integer examId, String studentId, Integer questionId, Double score) {
      return baseMapper.updateEssayQuestionScore(examId, studentId.toString(), questionId, score) > 0;
  }

  @Override
  @Transactional
  public boolean updateFillQuestionCorrectness(Integer examId, String studentId, Integer questionId, Boolean isCorrect) {
    try {
      QuestionAnswers question = questionAnswersService.getById(questionId);
      if (question == null) {
        throw new RuntimeException("题目不存在");
      }

      double score = isCorrect ? question.getScore() : 0;

      boolean success = baseMapper.updateFillQuestionCorrectness(examId, studentId, questionId, isCorrect) > 0;
      if (!success) {
        return false;
      }

      success = baseMapper.updateEssayQuestionScore(examId, studentId, questionId, score) > 0;
      if (!success) {
        return false;
      }

      LambdaQueryWrapper<StudentWrongAnswers> wrapper = new LambdaQueryWrapper<>();
      wrapper.eq(StudentWrongAnswers::getExamId, examId)
        .eq(StudentWrongAnswers::getStudentId, studentId);
      List<StudentWrongAnswers> answers = this.list(wrapper);

      double totalScore = answers.stream()
        .mapToDouble(StudentWrongAnswers::getScore)
        .sum();

      StudentGrade grade = studentGradeMapper.selectById(studentId);
      if (grade != null) {
        grade.setScore(totalScore);
        studentGradeMapper.updateById(grade);
      }

      return true;
    } catch (Exception e) {
      throw new RuntimeException("更新填空题正确性失败: " + e.getMessage());
    }
  }

  
}
