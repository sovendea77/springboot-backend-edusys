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
import java.time.LocalDateTime;

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
@TableName("question_answers")
public class QuestionAnswers implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("exam_id")
    private Integer examId;

    /**
     * 题目栏序号
     */
    @TableField("section_index")
    private Integer sectionIndex;

    /**
     * 题目类型：choice, fill, judgment
     */
    @TableField("section_type")
    private String sectionType;

    /**
     * 题目序号
     */
    @TableField("question_number")
    private Integer questionNumber;

    /**
     * 中文题号，如：一、二、三
     */
    @TableField("chinese_number")
    private String chineseNumber;

    /**
     * 题目内容
     */
    @TableField("content")
    private String content;

    /**
     * 答案内容
     */
    @TableField("answer")
    private String answer;

    /**
     * 分值
     */
    @TableField("score")
    private Integer score;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
