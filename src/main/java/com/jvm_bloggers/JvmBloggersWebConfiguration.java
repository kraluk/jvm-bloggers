package com.jvm_bloggers;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.http.MediaType.APPLICATION_ATOM_XML;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Configuration
class JvmBloggersWebConfiguration implements WebMvcConfigurer {

    @Override
    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
        configurer
            .favorPathExtension(true)
            .favorParameter(false)
            .ignoreAcceptHeader(false)
            .defaultContentType(APPLICATION_ATOM_XML)
            .mediaType("xml", APPLICATION_ATOM_XML)
            .mediaType("json", APPLICATION_JSON);
    }
}
