package com.example.springbootbackendedusys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.springbootbackendedusys"})
@MapperScan("com.example.springbootbackendedusys.mapper")
public class SpringbootBackendEdusysApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringbootBackendEdusysApplication.class, args);
  }

}
