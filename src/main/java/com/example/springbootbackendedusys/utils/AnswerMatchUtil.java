package com.example.springbootbackendedusys.utils;

import java.util.Arrays;
import java.util.List;

public class AnswerMatchUtil {
    private static final List<String> TRUE_ANSWERS = Arrays.asList("√", "✓", "对", "true", "t", "1", "yes", "y");
    private static final List<String> FALSE_ANSWERS = Arrays.asList("×", "✗", "错", "false", "f", "0", "no", "n");

    public static boolean isAnswerMatched(String studentAnswer, String standardAnswer) {
        if (studentAnswer == null || standardAnswer == null) {
            return false;
        }

        // 转换为字符串并去除空格
        String cleanStudentAnswer = studentAnswer.trim().toLowerCase();
        String cleanStandardAnswer = standardAnswer.trim().toLowerCase();

        // 完全匹配
        if (cleanStudentAnswer.equals(cleanStandardAnswer)) {
            return true;
        }

        // 判断题匹配
        if (TRUE_ANSWERS.contains(cleanStudentAnswer)) {
            return TRUE_ANSWERS.contains(cleanStandardAnswer);
        }
        if (FALSE_ANSWERS.contains(cleanStudentAnswer)) {
            return FALSE_ANSWERS.contains(cleanStandardAnswer);
        }

        // 数字匹配
        if (cleanStandardAnswer.matches("^\\d+(\\.\\d+)?$") && 
            cleanStudentAnswer.matches("^\\d+(\\.\\d+)?$")) {
            try {
                return Double.parseDouble(cleanStudentAnswer) == Double.parseDouble(cleanStandardAnswer);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        // 文本匹配（忽略标点符号和空格）
        String normalizedStudent = normalizeText(cleanStudentAnswer);
        String normalizedStandard = normalizeText(cleanStandardAnswer);
        return normalizedStudent.equals(normalizedStandard);
    }

    private static String normalizeText(String text) {
        return text.replaceAll("[.,，。、；;：:\"''\"\\s]", "");
    }
}