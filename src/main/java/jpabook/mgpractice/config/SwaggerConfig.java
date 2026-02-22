package jpabook.mgpractice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // 보안 스키마의 이름을 지정
        String jwtSchemeName = "jwtAuth";

        // API 요청을 보낼 때 위에서 지정한 보안 스키마를 요구 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // 보안 스키마가 어떤 방식(Bearer 토큰)인지 정의하여 컴포넌트에 추가
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));

        // 설정한 정보들을 OpenAPI 객체에 담아서 반환
        return new OpenAPI()
                .info(new Info().title("MG-Practice API")
                        .description("회원 및 계좌 관리 API 명세서입니다.")
                        .version("v1.0.0"))
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}