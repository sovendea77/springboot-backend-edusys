package com.example.springbootbackendedusys.service.impl;

import com.example.springbootbackendedusys.entity.QuestionAnswers;
import com.example.springbootbackendedusys.mapper.QuestionAnswersMapper;
import com.example.springbootbackendedusys.service.IQuestionAnswersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;


import java.util.List;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sovendea
 * @since 2025-05-12
 */
@Service
public class QuestionAnswersServiceImpl extends ServiceImpl<QuestionAnswersMapper, QuestionAnswers> implements IQuestionAnswersService {

  @Override
  public List<QuestionAnswers> getQuestionAnswersByExamId(Integer examId) {
    LambdaQueryWrapper<QuestionAnswers> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(QuestionAnswers::getExamId, examId)
      .orderByAsc(QuestionAnswers::getChineseNumber)
      .orderByAsc(QuestionAnswers::getQuestionNumber);
    return this.list(wrapper);
  }
}

