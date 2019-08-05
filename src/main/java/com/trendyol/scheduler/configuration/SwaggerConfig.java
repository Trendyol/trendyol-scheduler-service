package com.trendyol.scheduler.configuration;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static com.google.common.base.Predicates.not;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String PACKAGE_ORG_SPRINGFRAMEWORK = "org.springframework";
    private static final String DESCRIPTION = "Trendyol MP Excel API";
    private static final String DOT = ".";
    private static final String VENDOR = "vendor";
    private static final String TRENDYOL = "Trendyol";
    private static final String EMAIL_MARKETPLACE_DEV = "marketplace_dev@trendyol.com";
    private static final Contact TRENDYOL_CONTACT = new Contact(TRENDYOL, StringUtils.EMPTY, EMAIL_MARKETPLACE_DEV);

    @Value("${swagger.ignoredParameterTypes:#{T(org.apache.commons.lang3.ArrayUtils).EMPTY_CLASS_ARRAY}}")
    private Class[] ignoredParameterTypes;

    @Value("${spring.application.name:Api Documentation}")
    private String appName;

    @Value("${APP_VERSION:UNKNOWN_API_VERSION}")
    private String appVersion;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(not(RequestHandlerSelectors.basePackage(PACKAGE_ORG_SPRINGFRAMEWORK)))
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(ignoredParameterTypes)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                appName,
                DESCRIPTION,
                appVersion,
                StringUtils.EMPTY,
                TRENDYOL_CONTACT,
                DOT,
                DOT,
                Collections.singletonList(new StringVendorExtension(VENDOR, TRENDYOL))
        );
    }
}
