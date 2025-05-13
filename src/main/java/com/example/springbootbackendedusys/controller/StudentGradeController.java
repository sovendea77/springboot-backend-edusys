package com.example.springbootbackendedusys.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.springbootbackendedusys.entity.StudentGrade;
import com.example.springbootbackendedusys.service.IStudentGradeService;
import com.example.springbootbackendedusys.service.IStudentWrongAnswersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.springbootbackendedusys.common.R;
import com.example.springbootbackendedusys.service.IExamsService;
import com.example.springbootbackendedusys.entity.Exams;

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
public class StudentGradeController {

  @Autowired
  private IStudentGradeService studentGradeService;

  @Autowired
  private IStudentWrongAnswersService studentWrongAnswersService;

  @Autowired
  private IExamsService examsService;

  @PostMapping("/wrong-answers")
  public R saveStudentWrongAnswers(@RequestBody Map<String, Object> wrongAnswers) {
    try {
      return R.ok().message("错题保存成功");
    } catch (Exception e) {
      return R.error().message("错题保存失败");
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

  @GetMapping("/exam-statistics/{examId}")
  public R getExamStatistics(@PathVariable Long examId) {
    try {
      Exams exam = examsService.getById(examId);
      if (exam == null) {
        return R.error().message("考试不存在");
      }

      List<StudentGrade> grades = studentGradeService.lambdaQuery()
        .eq(StudentGrade::getExamId, examId)
        .list();

      Map<String, Object> statistics = new HashMap<>();

      double totalScore = exam.getTotalScore();
      statistics.put("totalScore", totalScore);

      // 计算其他统计数据
      int studentCount = grades.size();
      statistics.put("studentCount", studentCount);

      if (studentCount > 0) {
        double highestScore = grades.stream()
          .mapToDouble(StudentGrade::getScore)
          .max()
          .orElse(0.0);

        double lowestScore = grades.stream()
          .mapToDouble(StudentGrade::getScore)
          .min()
          .orElse(0.0);

        double averageScore = grades.stream()
          .mapToDouble(StudentGrade::getScore)
          .average()
          .orElse(0.0);

        String highestScoreStudent = grades.stream()
          .filter(g -> g.getScore() == highestScore)
          .map(StudentGrade::getStudentName)
          .findFirst()
          .orElse(null);

        statistics.put("highestScore", highestScore);
        statistics.put("lowestScore", lowestScore);
        statistics.put("averageScore", averageScore);
        statistics.put("highestScoreStudent", highestScoreStudent);
      } else {
        statistics.put("highestScore", 0);
        statistics.put("lowestScore", 0);
        statistics.put("averageScore", 0);
        statistics.put("highestScoreStudent", null);
      }

      return R.ok().data(statistics);
    } catch (Exception e) {
      return R.error().message("获取统计信息失败");
    }
  }



  @PostMapping("/save-grades")
  public R saveStudentGrades(@RequestBody List<StudentGrade> grades) {
    boolean success = studentGradeService.saveBatch(grades);
    return success ? R.ok().message("成绩保存成功") : R.error().message("成绩保存失败");
  }

  @GetMapping("/list/{examId}")
  public R getStudentList(@PathVariable Long examId) {
    try {
      List<StudentGrade> grades = studentGradeService.lambdaQuery()
        .eq(StudentGrade::getExamId, examId)
        .list();

      List<Map<String, Object>> formattedGrades = grades.stream()
        .map(grade -> {
          Map<String, Object> map = new HashMap<>();
          map.put("student_name", grade.getStudentName());
          map.put("score", grade.getScore());
          return map;
        })
        .collect(Collectors.toList());

      return R.ok().data(formattedGrades);
    } catch (Exception e) {
      return R.error().message("获取学生列表失败");
    }
  }
}
