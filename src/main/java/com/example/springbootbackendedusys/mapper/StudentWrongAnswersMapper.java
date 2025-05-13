package com.example.springbootbackendedusys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootbackendedusys.entity.StudentWrongAnswers;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author sovendea
 * @since 2025-05-12
 */
public interface StudentWrongAnswersMapper extends BaseMapper<StudentWrongAnswers> {

  @Select("SELECT swa.id, swa.student_answer, swa.question_id, swa.is_corrected, " +
    "qa.question_number, qa.chinese_number, qa.content, qa.answer as correct_answer, " +
    "qa.section_type, qa.score " +
    "FROM student_wrong_answers swa " +
    "JOIN question_answers qa ON swa.question_id = qa.id " +
    "WHERE swa.exam_id = #{examId} AND swa.student_id = #{studentId} AND swa.is_corrected = false " +
    "ORDER BY qa.chinese_number, qa.question_number")
  List<Map<String, Object>> selectWrongAnswersWithQuestionInfo(
    @Param("examId") Integer examId,
    @Param("studentId") String studentId);

  @Select("SELECT qa.id as question_id, qa.question_number, qa.chinese_number, " +
    "qa.content, qa.answer as correct_answer, qa.section_type, " +
    "COUNT(CASE WHEN swa.is_corrected = false THEN 1 END) as error_count " +
    "FROM question_answers qa " +
    "LEFT JOIN student_wrong_answers swa ON qa.id = swa.question_id " +
    "WHERE qa.exam_id = #{examId} " +
    "GROUP BY qa.id, qa.question_number, qa.chinese_number, qa.content, qa.answer, qa.section_type " +
    "HAVING error_count >= #{minErrorCount} " +
    "ORDER BY error_count DESC")
  List<Map<String, Object>> selectWrongAnswersAnalysis(
    @Param("examId") Integer examId,
    @Param("minErrorCount") Integer minErrorCount);

  @Update("UPDATE student_wrong_answers SET score = #{score} " +
    "WHERE exam_id = #{examId} AND student_id = #{studentId} AND question_id = #{questionId}")
  int updateEssayQuestionScore(
    @Param("examId") Integer examId,
    @Param("studentId") Long studentId,
    @Param("questionId") Integer questionId,
    @Param("score") Double score);

  @Update("UPDATE student_wrong_answers SET is_corrected = #{isCorrect} " +
    "WHERE exam_id = #{examId} AND student_id = #{studentId} AND question_id = #{questionId}")
  int updateFillQuestionCorrectness(
    @Param("examId") Integer examId,
    @Param("studentId") Long studentId,
    @Param("questionId") Integer questionId,
    @Param("isCorrect") Boolean isCorrect);
}
