package com.example.springbootbackendedusys.service;

import com.example.springbootbackendedusys.entity.Exams;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import com.example.springbootbackendedusys.entity.Exams;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sovendea
 * @since 2025-05-12
 */
@Service
public interface IExamsService extends IService<Exams> {
  boolean deleteExam(Integer examId);
  boolean updateTotalScore(Integer examId, Integer totalScore);
}
