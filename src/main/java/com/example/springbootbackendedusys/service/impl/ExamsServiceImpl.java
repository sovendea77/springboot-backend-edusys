package com.example.springbootbackendedusys.service.impl;

import com.example.springbootbackendedusys.entity.Exams;
import com.example.springbootbackendedusys.mapper.ExamsMapper;
import com.example.springbootbackendedusys.service.IExamsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.example.springbootbackendedusys.entity.Exams;
import com.example.springbootbackendedusys.mapper.ExamsMapper;
import com.example.springbootbackendedusys.service.IExamsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sovendea
 * @since 2025-05-12
 */
@Service
public class ExamsServiceImpl extends ServiceImpl<ExamsMapper, Exams> implements IExamsService {

  @Override
  @Transactional
  public boolean deleteExam(Integer examId) {
    // 删除考试及其关联的答案
    return removeById(examId);
  }

  @Override
  public boolean updateTotalScore(Integer examId, Integer totalScore) {
    Exams exam = new Exams();
    exam.setId(examId);
    exam.setTotalScore(totalScore);
    return updateById(exam);
  }
}
