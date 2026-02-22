package jpabook.mgpractice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MG 뱅킹 시스템 API 명세서")
                        .version("v1.0.0")
                        .description("금융 IT 인턴십 대비 뱅킹 트랜잭션 API 문서입니다."));
    }
}