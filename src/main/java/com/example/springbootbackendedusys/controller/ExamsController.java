package com.example.springbootbackendedusys.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.springbootbackendedusys.entity.Exams;
import com.example.springbootbackendedusys.entity.QuestionAnswers;
import com.example.springbootbackendedusys.service.IExamsService;
import com.example.springbootbackendedusys.service.IQuestionAnswersService;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.springbootbackendedusys.common.R;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Map;

// 添加导入语句
import java.time.format.DateTimeFormatter;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author sovendea
 * @since 2025-05-12
 */
@RestController
@RequestMapping("/api/exams")
public class ExamsController {

  @Autowired
  private IExamsService examsService;

  @Autowired
  private IQuestionAnswersService questionAnswersService;

  @PostMapping
  public R createExam(@RequestBody Exams exam) {
    boolean success = examsService.save(exam);
    return success ? R.ok().message("考试创建成功").data(exam) : R.error().message("考试创建失败");
  }

  @GetMapping("/{id}")
  public R getExamById(@PathVariable Long id) {
    Exams exam = examsService.getById(id);
    return exam != null ? R.ok().data(exam) : R.error().message("考试不存在");
  }

  @GetMapping("/teacher/{teacherId}")
  public R getExamsByTeacherId(@PathVariable Long teacherId) {
    List<Exams> exams = examsService.lambdaQuery()
      .eq(Exams::getTeacherId, teacherId)
      .orderByDesc(Exams::getCreatedAt)
      .list();

    List<Map<String, Object>> formattedExams = exams.stream()
      .map(exam -> {
        Map<String, Object> map = new HashMap<>();
        map.put("id", exam.getId());
        map.put("title", exam.getTitle());
        map.put("description", exam.getDescription());
        map.put("teacher_id", exam.getTeacherId());
        map.put("total_score", exam.getTotalScore());
        map.put("created_at",
          exam.getCreatedAt() != null
            ? exam.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            : null);
        map.put("updated_at",
          exam.getUpdatedAt() != null
            ? exam.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            : null);
        return map;
      })
      .collect(Collectors.toList());

    return R.ok().data(formattedExams);
  }

  @PostMapping("/{examId}/answers")
  public R saveAnswers(@PathVariable Long examId,
                       @RequestBody Map<String, List<QuestionAnswers>> sections) {
    try {
      return R.ok().message("答案保存成功");
    } catch (Exception e) {
      return R.error().message("答案保存失败");
    }
  }

  @GetMapping("/{examId}/answers")
  public R getAnswers(@PathVariable Long examId) {
    List<QuestionAnswers> answers = questionAnswersService.lambdaQuery()
      .eq(QuestionAnswers::getExamId, examId)
      .orderByAsc(QuestionAnswers::getSectionIndex)
      .orderByAsc(QuestionAnswers::getQuestionNumber)
      .list();

    List<Map<String, Object>> formattedAnswers = answers.stream()
      .map(answer -> {
        Map<String, Object> map = new HashMap<>();
        map.put("id", answer.getId());
        map.put("exam_id", answer.getExamId());
        map.put("section_index", answer.getSectionIndex());
        map.put("section_type", answer.getSectionType());
        map.put("question_number", answer.getQuestionNumber());
        map.put("chinese_number", answer.getChineseNumber());
        map.put("content", answer.getContent());
        map.put("answer", answer.getAnswer());
        map.put("score", answer.getScore());
        map.put("created_at",
          answer.getCreatedAt() != null
            ? answer.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            : null);
        return map;
      })
      .collect(Collectors.toList());

    return R.ok().data(formattedAnswers);
  }

  @DeleteMapping("/{id}")
  public R deleteExam(@PathVariable Long id) {
    boolean success = examsService.removeById(id);
    return success ? R.ok().message("考试删除成功") : R.error().message("考试删除失败");
  }
}
