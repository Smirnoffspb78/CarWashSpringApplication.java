package com.smirnov.carwashspring.configuration.file;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;


@Configuration
@ComponentScan
@EnableAutoConfiguration
public class FileConfiguration {

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        DataSize dataSize = DataSize.ofMegabytes(5);
        factory.setMaxFileSize(dataSize);
        factory.setMaxRequestSize(dataSize);
        return factory.createMultipartConfig();
    }
}