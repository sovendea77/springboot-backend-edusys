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

  // 原 router.get('/exam-statistics/:examId', studentController.getExamStatistics);
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


  // 原 router.post('/save-grades', studentController.saveStudentGrades);
  @PostMapping("/save-grades")
  public R saveStudentGrades(@RequestBody Map<String, Object> requestData) {
    try {
      Integer examId = Integer.valueOf(requestData.get("examId").toString());

      Exams exam = examsService.getById(examId);
      if (exam == null) {
        return R.error().message("考试不存在");
      }

      int savedCount = studentGradeService.calculateAndSaveGrades(examId);

      return R.ok()
        .message("成功保存 " + savedCount + " 名学生的成绩")
        .data(new HashMap<String, Object>() {{
          put("savedCount", savedCount);
        }});

    } catch (Exception e) {
      return R.error().message("保存学生成绩失败: " + e.getMessage());
    }
  }

  //原 router.get('/list/:examId', studentController.getStudentList);
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

  // 原 router.post('/save-answers-with-grades', studentController.saveStudentAnswersWithGrades);
  @PostMapping("/save-answers-with-grades")
  public R saveStudentAnswersWithGrades(@RequestBody Map<String, Object> requestData) {
    try {
      Integer examId = Integer.valueOf(requestData.get("examId").toString());
      String studentId = requestData.get("studentId").toString();
      String studentName = requestData.get("studentName").toString();
      Double totalScore = Double.valueOf(requestData.get("totalScore").toString());
      List<Map<String, Object>> answers = (List<Map<String, Object>>) requestData.get("answers");

      boolean success = studentWrongAnswersService.saveStudentAnswersWithGrades(
        examId,
        studentName,
        totalScore,
        answers
      );

      if (success) {
        return R.ok()
          .message("学生答案和批改结果保存成功")
          .data(new HashMap<String, Object>() {{
            put("examId", examId);
            put("studentId", studentId);
            put("studentName", studentName);
            put("totalScore", totalScore);
            put("savedCount", answers.size());
          }});
      } else {
        return R.error().message("保存失败");
      }
    } catch (Exception e) {
      return R.error().message("保存学生答案和批改结果失败: " + e.getMessage());
    }
  }
}
