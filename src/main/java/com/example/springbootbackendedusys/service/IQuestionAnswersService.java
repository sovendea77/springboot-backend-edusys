package com.example.springbootbackendedusys.service;

import com.example.springbootbackendedusys.entity.QuestionAnswers;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.springbootbackendedusys.entity.QuestionAnswers;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sovendea
 * @since 2025-05-12
 */
@Service
public interface IQuestionAnswersService extends IService<QuestionAnswers> {
  List<QuestionAnswers> getQuestionAnswersByExamId(Integer examId);
}
