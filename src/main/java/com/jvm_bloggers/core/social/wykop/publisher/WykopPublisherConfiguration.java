package com.jvm_bloggers.core.social.wykop.publisher;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import static com.jvm_bloggers.ApplicationProfiles.DEV;
import static com.jvm_bloggers.ApplicationProfiles.PRODUCTION;
import static com.jvm_bloggers.ApplicationProfiles.STAGE;
import static com.jvm_bloggers.ApplicationProfiles.TEST;

@EnableConfigurationProperties(WykopPublisherConfiguration.WykopProperties.class)
@Configuration
class WykopPublisherConfiguration {

    @Profile(PRODUCTION)
    @Bean
    WykopPublisher wykopRestPublisher(final WykopProperties properties) {
        final var client = wykopRestClient();
        final var callSigner = new RestPublisher.CallSigner();

        return new RestPublisher(client, callSigner, properties);
    }

    @Profile({DEV, STAGE, TEST})
    @Bean
    WykopPublisher wykopLogPublisher() {
        return new LogPublisher();
    }

    Client wykopRestClient() {
        return ClientBuilder.newClient();
    }

    @ConfigurationProperties("wykop.api")
    record WykopProperties(String apiUrl,
                           String key,
                           String secret) {
    }
}
