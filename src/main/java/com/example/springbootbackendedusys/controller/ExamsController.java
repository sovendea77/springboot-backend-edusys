package com.example.springbootbackendedusys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.springbootbackendedusys.entity.StudentGrade;
import com.example.springbootbackendedusys.service.IStudentGradeService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.springbootbackendedusys.entity.Exams;
import com.example.springbootbackendedusys.entity.QuestionAnswers;
import com.example.springbootbackendedusys.service.IExamsService;
import com.example.springbootbackendedusys.service.IQuestionAnswersService;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.springbootbackendedusys.common.R;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

  @Autowired
  private IStudentGradeService studentGradeService;

  @PostMapping
  public R createExam(@RequestBody Map<String, Object> examData) {

    String title = (String) examData.get("title");
    Integer teacherId = examData.get("teacher_id") instanceof Number ?
      ((Number) examData.get("teacher_id")).intValue() : null;
    String description = (String) examData.get("description");

    if (StringUtils.isEmpty(title) || teacherId == null) {
      return R.error().message("缺少必要字段");
    }

    try {
      Exams exam = new Exams();
      exam.setTitle(title);
      exam.setTeacherId(teacherId);
      exam.setDescription(description);
      exam.setCreatedAt(LocalDateTime.now());
      exam.setUpdatedAt(LocalDateTime.now());

      boolean success = examsService.save(exam);

      if (success) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", exam.getId());
        return R.ok().data(data);
      } else {
        return R.error().message("考试创建失败");
      }
    } catch (Exception e) {
      return R.error().message("创建考试失败: " + e.getMessage());
    }
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
  public R saveAnswers(@PathVariable Integer examId, @RequestBody Map<String, Object> requestBody) {
    try {

      // 验证考试是否存在
      Exams exam = examsService.getById(examId);
      if (exam == null) {
        return R.error().message("考试不存在");
      }

      // 获取sections数组
      List<Map<String, Object>> sections = (List<Map<String, Object>>) requestBody.get("sections");
      if (sections == null || sections.isEmpty()) {
        return R.error().message("没有题目数据");
      }

      // 准备要保存的答案数据
      List<QuestionAnswers> answersToSave = new ArrayList<>();
      int totalScore = 0;

      // 处理每个题型section
      for (Map<String, Object> section : sections) {
        String type = (String) section.get("type");
        Integer score = ((Number) section.get("score")).intValue();
        String chineseNumber = (String) section.get("chineseNumber");
        List<Map<String, Object>> questions = (List<Map<String, Object>>) section.get("questions");

        if (questions != null) {
          for (int i = 0; i < questions.size(); i++) {
            Map<String, Object> question = questions.get(i);
            QuestionAnswers answer = new QuestionAnswers();
            answer.setExamId(examId);
            answer.setSectionType(type);
            answer.setSectionIndex(sections.indexOf(section) + 1);
            answer.setQuestionNumber(((Number) section.get("startNum")).intValue() + i);
            answer.setChineseNumber(chineseNumber);
            answer.setContent((String) question.get("content"));
            answer.setAnswer((String) question.get("answer"));
            answer.setScore(score);
            answer.setCreatedAt(LocalDateTime.now());

            answersToSave.add(answer);
            totalScore += score;
          }
        }
      }
      // 先删除旧答案
      questionAnswersService.remove(new QueryWrapper<QuestionAnswers>()
        .eq("exam_id", examId));

      studentGradeService.remove(new QueryWrapper<StudentGrade>()
        .eq("exam_id", examId));

      // 保存新答案
      boolean success = questionAnswersService.saveBatch(answersToSave);

      // 更新考试总分
      exam.setTotalScore(totalScore);
      examsService.updateById(exam);

      if (success) {
        Map<String, Object> data = new HashMap<>();
        data.put("answersCount", answersToSave.size());
        data.put("totalScore", totalScore);
        return R.ok().message("答案保存成功").data(data);
      } else {
        return R.error().message("答案保存失败");
      }

    } catch (Exception e) {
      return R.error().message("保存答案失败: " + e.getMessage());
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
  public R deleteExam(@PathVariable Integer id) {
    try {
      // 验证考试是否存在
      Exams exam = examsService.getById(id);
      if (exam == null) {
        return R.error().message("考试不存在");
      }

      // 先删除关联的答案
      questionAnswersService.remove(new QueryWrapper<QuestionAnswers>()
        .eq("exam_id", id));

      // 删除考试
      boolean success = examsService.removeById(id);

      return success ?
        R.ok().message("考试删除成功") :
        R.error().message("考试删除失败");

    } catch (Exception e) {
      return R.error().message("删除考试失败: " + e.getMessage());
    }
  }
}
