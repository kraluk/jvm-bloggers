package com.jvm_bloggers.core.social.wykop.publisher;

import com.jvm_bloggers.entities.wykop.Wykop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LogPublisher implements WykopPublisher {
    private static final Logger log = LoggerFactory.getLogger(LogPublisher.class);

    @Override
    public WykopPublishingStatus publish(final Wykop wykop) {
        log.debug("Publishing on Wykop - '{}'", wykop.getContent());
        return WykopPublishingStatus.SUCCESS;
    }
}
