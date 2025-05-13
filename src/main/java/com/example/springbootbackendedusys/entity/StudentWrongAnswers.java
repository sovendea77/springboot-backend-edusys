package com.example.springbootbackendedusys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author sovendea
 * @since 2025-05-12
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("student_wrong_answers")
public class StudentWrongAnswers implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生姓名
     */
    @TableField("student_id")
    private String studentId;

    @TableField("question_id")
    private Integer questionId;

    @TableField("exam_id")
    private Integer examId;

    /**
     * 学生填写的答案
     */
    @TableField("student_answer")
    private String studentAnswer;

    /**
     * 是否正确
     */
    @TableField("is_corrected")
    private Boolean isCorrected;

    @TableField("score")
    private Double score;
}
