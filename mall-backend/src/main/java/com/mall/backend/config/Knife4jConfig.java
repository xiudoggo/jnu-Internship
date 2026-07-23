package com.mall.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("潮购电商平台接口文档")
                .version("1.0.0")
                .description("基于 Spring Boot 3 + MyBatis-Plus 的电商平台后端 RESTful API 文档")
                .contact(new Contact()
                    .name("开发团队")
                    .email("dev@example.com")));
    }
}