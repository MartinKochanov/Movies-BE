package com.mk.movies.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        var server = new Server()
            .url("http://localhost:8080")
            .description("Development server");

        var info = new Info()
            .title("Movies API")
            .version("v1.0")
            .description(
                "API for managing movies including cast, producers, writers, and directors");

        return new OpenAPI()
            .info(info)
            .servers(List.of(server));
    }
}
