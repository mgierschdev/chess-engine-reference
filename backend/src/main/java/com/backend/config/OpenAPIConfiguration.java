package com.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for the Chess API.
 * 
 * Provides interactive API documentation at /swagger-ui.html
 */
@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI chessEngineOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chess Engine API")
                        .description("REST API for a chess game with move validation, check/checkmate detection, " +
                                    "and special moves (en passant, castling, pawn promotion). " +
                                    "Designed for two human players on the same machine.")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Chess Engine")
                                .url("https://github.com/mgierschdev/ChessEngine")));
    }
}
