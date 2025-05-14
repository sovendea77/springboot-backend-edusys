package com.example.springbootbackendedusys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
@TableName("exams")
public class Exams implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("teacher_id")
    private Integer teacherId;

    /**
     * 试卷总分
     */
    @TableField("total_score")
    private Integer totalScore;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("duration")
    private Integer duration;

    @TableField("exam_date")
    private LocalDateTime examDate;

    @TableField("status")
    private String status;

}
