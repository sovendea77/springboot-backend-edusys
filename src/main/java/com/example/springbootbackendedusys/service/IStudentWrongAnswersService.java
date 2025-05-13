package com.example.springbootbackendedusys.service;

import com.example.springbootbackendedusys.entity.StudentWrongAnswers;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springbootbackendedusys.entity.StudentWrongAnswers;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sovendea
 * @since 2025-05-12
 */
@Service
public interface IStudentWrongAnswersService extends IService<StudentWrongAnswers> {
  List<Map<String, Object>> getStudentWrongAnswers(Integer examId, String studentId);
  List<Map<String, Object>> getWrongAnswersAnalysis(Integer examId, Integer minErrorCount);
  boolean updateEssayQuestionScore(Integer examId, Long studentId, Integer questionId, Double score);
  boolean updateFillQuestionCorrectness(Integer examId, Long studentId, Integer questionId, Boolean isCorrect);
  boolean saveStudentAnswersWithGrades(Integer examId, Long studentId, String studentName, Double totalScore, List<Map<String, Object>> answers);
}

