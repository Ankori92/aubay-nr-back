package com.aubay.formations.nr.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket api() throws IOException {
        Properties properties = new Properties();
        properties.load(SwaggerConfiguration.class.getResourceAsStream("/swagger.properties"));
        // Change this property to a parent level to scan generated and src code
        String packageName = properties.getProperty("openapi.api.package");

        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage(packageName))
                .paths(PathSelectors.regex("/.*"))
                .build()
                .apiInfo(apiEndPointsInfo());
    }

    private ApiInfo apiEndPointsInfo() throws IOException {
        Properties properties = new Properties();
        properties.load(SwaggerConfiguration.class.getResourceAsStream("/swagger.properties"));
        String name = properties.getProperty("openapi.api.name");
        String version = properties.getProperty("openapi.api.version");
        String description = properties.getProperty("openapi.api.description");
        return new ApiInfoBuilder().title(name)
                .description(description)
                .version(version)
                .build();
    }
}
