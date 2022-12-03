package com.jvm_bloggers.core.social.wykop;

import com.jvm_bloggers.core.blogpost_redirect.LinkGenerator;
import com.jvm_bloggers.entities.wykop.WykopRepository;
import com.jvm_bloggers.utils.NowProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class WykopConfiguration {

    @Bean
    WykopProducer wykopProducer(final WykopRepository repository,
                                final LinkGenerator linkGenerator,
                                final NowProvider nowProvider) {
        final var contentGenerator = contentGenerator();

        return new WykopProducer(contentGenerator, repository, linkGenerator, nowProvider);
    }

    WykopContentGenerator contentGenerator() {
        return new WykopContentGenerator();
    }
}

