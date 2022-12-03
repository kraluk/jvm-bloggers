package com.jvm_bloggers.core.social.wykop.publisher;

import com.jvm_bloggers.entities.wykop.Wykop;

interface WykopPublisher {

    WykopPublishingStatus publish(final Wykop wykop);

    enum WykopPublishingStatus {
        SUCCESS,
        ERROR;

        public boolean isOk() {
            return this.equals(SUCCESS);
        }
    }
}
