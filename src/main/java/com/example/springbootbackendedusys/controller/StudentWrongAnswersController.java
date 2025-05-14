package com.example.springbootbackendedusys.controller;

import com.example.springbootbackendedusys.entity.StudentGrade;
import com.example.springbootbackendedusys.service.IStudentGradeService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.springbootbackendedusys.service.IStudentWrongAnswersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.springbootbackendedusys.common.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author sovendea
 * @since 2025-05-12
 */
@RestController
@RequestMapping("/api/students")
public class StudentWrongAnswersController {

  @Autowired
  private IStudentWrongAnswersService studentWrongAnswersService;

  @Autowired
  private IStudentGradeService studentGradeService;



  @GetMapping("/wrong-answers-analysis/{examId}")
  public R getWrongAnswersAnalysis(
    @PathVariable Integer examId,
    @RequestParam(defaultValue = "1") Integer minErrorCount) {
    try {
      List<Map<String, Object>> analysis = studentWrongAnswersService.getWrongAnswersAnalysis(examId, minErrorCount);
      return R.ok().data(analysis);
    } catch (Exception e) {
      return R.error().message("获取错题分析数据失败: " + e.getMessage());
    }
  }

  @GetMapping("/wrong-answers/{examId}/{studentId}")
  public R getStudentWrongAnswers(@PathVariable Integer examId, @PathVariable String studentId) {
    try {
      List<Map<String, Object>> wrongAnswers = studentWrongAnswersService.getStudentWrongAnswers(examId, studentId);

      List<Map<String, Object>> formattedAnswers = wrongAnswers.stream()
        .map(answer -> {
          Map<String, Object> formatted = new HashMap<>();
          formatted.put("id", answer.get("id"));
          formatted.put("student_answer", answer.get("studentAnswer"));
          formatted.put("question_id", answer.get("questionId"));
          formatted.put("is_corrected", answer.get("isCorrected"));
          formatted.put("question_number", answer.get("questionNumber"));
          formatted.put("chinese_number", answer.get("chineseNumber"));
          formatted.put("content", answer.get("content"));
          formatted.put("correct_answer", answer.get("correctAnswer"));
          formatted.put("section_type", answer.get("sectionType"));
          formatted.put("score", answer.get("score"));
          return formatted;
        })
        .collect(Collectors.toList());

      return R.ok().data(formattedAnswers);
    } catch (Exception e) {
      return R.error().message("获取学生错题详情失败: " + e.getMessage());
    }
  }

  @PostMapping("/fill-correctness")
  public R updateFillQuestionScore(
      @RequestBody Map<String, Object> requestData) {
      try {
          Integer examId = Integer.valueOf(requestData.get("examId").toString());
          String studentId = requestData.get("studentId").toString();
          Integer questionId = Integer.valueOf(requestData.get("questionId").toString());
          boolean isCorrect = Boolean.parseBoolean(requestData.get("isCorrect").toString());

          // 更新填空题得分和正确性
          boolean success = studentWrongAnswersService.updateFillQuestionCorrectness(
              examId,
              studentId,
              questionId,
              isCorrect
          );

          if (!success) {
              return R.error().message("更新填空题评分失败");
          }

          // 获取当前学生的总分
          StudentGrade currentGrade = studentGradeService.lambdaQuery()
              .eq(StudentGrade::getExamId, examId)
              .eq(StudentGrade::getStudentName, studentId)
              .one();

          // 计算新的总分
          double totalScore = currentGrade != null ? currentGrade.getScore() : 0.0;

          Map<String, Object> updatedRecord = new HashMap<>();
          updatedRecord.put("examId", examId);
          updatedRecord.put("studentId", studentId);
          updatedRecord.put("questionId", questionId);
          updatedRecord.put("isFullScore", isCorrect);
          updatedRecord.put("totalScore", totalScore);

          return R.ok()
              .message("评分更新成功")
              .data(updatedRecord);

      } catch (Exception e) {
          return R.error().message("更新填空题评分失败: " + e.getMessage());
      }
  }

  @PostMapping("/essay-score")
  public R updateEssayQuestionScore(
      @RequestBody Map<String, Object> requestData) {
      try {
          Integer examId = Integer.valueOf(requestData.get("examId").toString());
          String studentId = requestData.get("studentId").toString();  // 保持为 String 类型
          Integer questionId = Integer.valueOf(requestData.get("questionId").toString());
          Double score = Double.valueOf(requestData.get("score").toString());

          // 更新主观题分数
          boolean success = studentWrongAnswersService.updateEssayQuestionScore(
              examId,
              studentId,  // 直接传递 studentId
              questionId,
              score
          );

          if (!success) {
              return R.error().message("更新主观题分数失败");
          }

          Map<String, Object> data = new HashMap<>();
          data.put("score", score);

          return R.ok()
              .message("主观题分数更新成功")
              .data(data);

      } catch (Exception e) {
          return R.error().message("更新主观题分数失败: " + e.getMessage());
      }
  }
}
