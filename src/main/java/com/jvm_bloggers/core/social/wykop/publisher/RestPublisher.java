package com.jvm_bloggers.core.social.wykop.publisher;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.jvm_bloggers.core.social.wykop.publisher.WykopPublisherConfiguration.WykopProperties;
import com.jvm_bloggers.entities.wykop.Wykop;

import io.vavr.control.Option;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

class RestPublisher implements WykopPublisher {
    private static final Logger log = LoggerFactory.getLogger(LogPublisher.class);

    private final Client client;
    private final CallSigner callSigner;
    private final WykopProperties properties;

    RestPublisher(final Client client,
                  final CallSigner callSigner,
                  final WykopProperties properties) {
        this.client = client;
        this.callSigner = callSigner;
        this.properties = properties;
    }

    @Override
    public WykopPublishingStatus publish(final Wykop wykop) {
        final var request = Entity.entity(new Request(""), MediaType.APPLICATION_JSON_TYPE);

        return Option.of(client)
            .map(this::callTarget)
            .peek(t -> log.info("Using - '{}'", t.getUri()))
            .toTry()
            .map(WebTarget::request)
            .map(this::applySignHeader)
            .mapTry(r -> r.post(request, Response.class))
            .onSuccess(r -> log.info("Got response - '{}'", r))
            .onFailure(t -> log.error("Unable to post on Wykop!", t))
            .map(r -> WykopPublishingStatus.SUCCESS)
            .recover(Exception.class, WykopPublishingStatus.ERROR)
            .get();
    }

    private Invocation.Builder applySignHeader(final Invocation.Builder builder) {
        return builder.header("apisign", callSigner.sign());
    }

    private WebTarget callTarget(final Client client) {
        return client
            .target("{api_url}/repos/{org}/{repo}/contributors?per_page={page_size}")
            .resolveTemplate("api_url", "value", false);
    }

    record Request(String data) {
    }

    record Response(String data) {
    }

    static class CallSigner {

        @SuppressWarnings("all")
        String sign() {
            return Hashing
                .md5()
                .hashString("example", Charsets.UTF_8)
                .toString();
        }
    }
}
