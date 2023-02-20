package com.example.cryptorecommendation.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Load project info properties from pom.xml via application.yml.
 *
 */
@ConfigurationProperties("application.pom")
public record ApplicationProperties(String name, String version, String description) {
}