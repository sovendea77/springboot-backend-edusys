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
@TableName("student_grade")
public class StudentGrade implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学生姓名
     */
    @TableField("student_name")
    private String studentName;

    @TableField("exam_id")
    private Integer examId;

    @TableField("score")
    private Double score;
}
