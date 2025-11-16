package com.example.movies;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("IMDb Management API")
                        .description("""
                                This API acts as a gateway to IMDb RapidAPI services.
                                It allows you to fetch movie, series, people, ratings, 
                                search results and more — while logging every request 
                                into the system for analytics.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Beqa")
                                .email("beqabeqa329@gmail.com")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")
                        )
                        .termsOfService("https://your-domain.com/terms")
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Project GitHub Repository")
                        .url("https://github.com/beqa9/movies-api")
                );
    }
}
