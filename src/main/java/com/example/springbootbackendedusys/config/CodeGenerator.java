package com.example.springbootbackendedusys.config;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.model.ClassAnnotationAttributes;

import java.nio.file.Paths;
import java.util.Collections;

/**
 * @author Sovendea
 * @version 1.0
 * Create by 2025/5/12 13:50
 */
public class CodeGenerator {
  public static void main(String[] args) {
    // 使用 FastAutoGenerator 快速配置代码生成器
    FastAutoGenerator.create("jdbc:mysql://localhost:3306/edusystem?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT%2B8", "root", "sovendea")
      .globalConfig(builder -> {
        builder.outputDir(Paths.get(System.getProperty("user.dir"), "/src/main/java").toString())
          .author("sovendea");
      })
      .packageConfig(builder -> {
        builder.parent("com.example.springbootbackendedusys") // 设置父包名
          .entity("entity") // 设置实体类包名
          .mapper("mapper") // 设置 Mapper 接口包名
          .service("service") // 设置 Service 接口包名
          .serviceImpl("service.impl") // 设置 Service 实现类包名
          .xml("mappers") // 设置 Mapper XML 文件包名
          .pathInfo(Collections.singletonMap(
            OutputFile.xml,
            "src/main/resources/mappers" // 直接指定资源目录下的路径
          ));
      })
      .strategyConfig(builder -> {
        builder.addInclude("teachers", "exams", "question_answers",
            "student_wrong_answers", "student_grade") // 设置需要生成的表名
          .entityBuilder()
          .enableLombok(new ClassAnnotationAttributes("@Data\n@Builder", "lombok.Data", "lombok.Builder")) // 启用 Lombok
          //.enableFileOverride()  //覆盖已生成文件,有需要启用
          .enableTableFieldAnnotation() // 启用字段注解
          .controllerBuilder()
          .enableRestStyle();// 启用 REST 风格
      })
      .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
      .execute(); // 执行生成
  }
}



