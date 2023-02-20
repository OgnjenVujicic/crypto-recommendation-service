package com.example.cryptorecommendation.configuration;

import com.example.cryptorecommendation.properties.ApplicationProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Open API configuration with project properties loaded from pom.xml.
 */
@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI openApi(ApplicationProperties properties) {
        return new OpenAPI().info(new Info().title(properties.name())
                        .description(properties.description())
                        .version(properties.version()));
    }
}
