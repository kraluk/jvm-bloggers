package com.jvm_bloggers.core.github

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import jakarta.ws.rs.client.ClientRequestContext
import jakarta.ws.rs.core.HttpHeaders
import jakarta.ws.rs.core.MultivaluedHashMap
import jakarta.ws.rs.core.MultivaluedMap

@Subject(GithubAuthenticationFilter)
class GithubAuthenticationFilterSpec extends Specification {

    public static final String TOKEN = 'TOKEN'

    def "Should set not empty access token"() {
        given:
        GithubProperties properties = new GithubProperties()
        properties.setToken(TOKEN)

        GithubAuthenticationFilter testObj = new GithubAuthenticationFilter(properties)

        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>()

        ClientRequestContext requestContext = Mock()
        requestContext.getHeaders() >> headers

        when:
        testObj.filter(requestContext)

        then:
        headers.getFirst(HttpHeaders.AUTHORIZATION) == 'token ' + TOKEN
    }

    @Unroll
    def "Should not set access token when access token is: #token"() {
        given:
        GithubProperties properties = new GithubProperties()
        properties.setToken(token)

        GithubAuthenticationFilter testObj = new GithubAuthenticationFilter(properties)

        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>()

        ClientRequestContext requestContext = Mock()
        requestContext.getHeaders() >> headers

        when:
        testObj.filter(requestContext)

        then:
        headers.getFirst(HttpHeaders.AUTHORIZATION) == null

        where:
        token << [null, ""]
    }
}
