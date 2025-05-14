package com.example.springbootbackendedusys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.springbootbackendedusys.entity.StudentWrongAnswers;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Delete;
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

  @Select("SELECT " +
    "swa.id, " +
    "swa.student_id AS studentId, " +
    "swa.student_answer AS studentAnswer, " +
    "swa.is_corrected AS isCorrected, " +
    "swa.score, " +
    "swa.question_id AS questionId, " +
    "qa.section_type AS sectionType, " +
    "qa.chinese_number AS chineseNumber, " +
    "qa.question_number AS questionNumber, " +
    "qa.content, " +
    "qa.answer AS correctAnswer " +
    "FROM student_wrong_answers swa " +
    "LEFT JOIN question_answers qa ON swa.question_id = qa.id " +
    "WHERE swa.exam_id = #{examId} AND swa.student_id = #{studentId} AND swa.is_corrected = false " +
    "ORDER BY qa.chinese_number, qa.question_number")
  List<Map<String, Object>> selectWrongAnswersWithQuestionInfo(
    @Param("examId") Integer examId,
    @Param("studentId") String studentId);

  @Select("SELECT " +
    "qa.id, " +
    "qa.question_number, " +
    "qa.chinese_number, " +
    "qa.content, " +
    "qa.answer as correct_answer, " +
    "qa.section_type, " +
    "COUNT(CASE WHEN swa.is_corrected = false THEN 1 END) as error_count " +
    "FROM student_wrong_answers swa " +
    "JOIN question_answers qa ON swa.question_id = qa.id " +
    "WHERE swa.exam_id = #{examId} " +
    "GROUP BY qa.id, qa.question_number, qa.chinese_number, qa.content, qa.answer, qa.section_type " +
    "HAVING COUNT(CASE WHEN swa.is_corrected = false THEN 1 END) >= #{minErrorCount} " +
    "ORDER BY error_count DESC")
  List<Map<String, Object>> selectWrongAnswersAnalysis(
    @Param("examId") Integer examId,
    @Param("minErrorCount") Integer minErrorCount);

  @Select("SELECT * FROM question_answers " +
          "WHERE exam_id = #{examId} AND chinese_number = #{chineseNumber} " +
          "AND question_number = #{questionNumber}")
  Map<String, Object> getQuestionInfo(
      @Param("examId") Integer examId,
      @Param("chineseNumber") String chineseNumber,
      @Param("questionNumber") Integer questionNumber);

  @Insert("INSERT INTO student_wrong_answers " +
      "(exam_id, student_id, question_id, student_answer, is_corrected, score, section_type) " +
      "VALUES (#{examId}, #{studentId}, #{questionId}, #{studentAnswer}, #{isCorrect}, #{score}, #{sectionType})")
  int saveStudentAnswer(
      @Param("examId") Integer examId,
      @Param("studentId") String studentId,
      @Param("questionId") Integer questionId,
      @Param("studentAnswer") String studentAnswer,
      @Param("isCorrect") Boolean isCorrect,
      @Param("score") Double score,
      @Param("sectionType") String sectionType);

  @Update("UPDATE student_wrong_answers " +
    "SET score = #{score}, is_corrected = #{score} > 0 " +
    "WHERE exam_id = #{examId} " +
    "AND student_id = #{studentId} " +
    "AND question_id = #{questionId}")
  int updateEssayQuestionScore(
    @Param("examId") Integer examId,
    @Param("studentId") String studentId,
    @Param("questionId") Integer questionId,
    @Param("score") Double score);

  @Update("UPDATE student_wrong_answers " +
    "SET is_corrected = #{isCorrect}, " +
    "score = CASE WHEN #{isCorrect} = true THEN " +
    "(SELECT score FROM question_answers WHERE id = #{questionId}) " +
    "ELSE 0 END " +
    "WHERE exam_id = #{examId} " +
    "AND student_id = #{studentId} " +
    "AND question_id = #{questionId}")
  int updateFillQuestionCorrectness(
    @Param("examId") Integer examId,
    @Param("studentId") String studentId,
    @Param("questionId") Integer questionId,
    @Param("isCorrect") Boolean isCorrect);


}
