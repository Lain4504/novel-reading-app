package com.miraimagiclab.novelreadingapp.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Novel Reading App API")
                    .description("API documentation for Novel Reading App - A platform for managing and reading novels")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("Mirai Magic Lab")
                            .email("contact@miraimagiclab.com")
                    )
                    .license(
                        License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")
                    )
            )
            .addServersItem(
                Server()
                    .url("http://localhost:8080/api")
                    .description("Development server")
            )
            .addServersItem(
                Server()
                    .url("https://api.novelreadingapp.com")
                    .description("Production server")
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        "bearerAuth",
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                            .description("Enter JWT token")
                    )
            )
            .addSecurityItem(
                SecurityRequirement().addList("bearerAuth")
            )
    }
}
