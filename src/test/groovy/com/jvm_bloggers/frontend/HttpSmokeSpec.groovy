package com.jvm_bloggers.frontend

import com.sun.net.httpserver.HttpServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import static org.springframework.http.HttpStatus.OK

@ContextConfiguration
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class HttpSmokeSpec extends Specification {

    @Shared
    static HttpServer githubStub

    @Autowired
    TestRestTemplate restTemplate

    def setupSpec() {
        System.setProperty("jasypt.encryptor.password", "secretPassword")
    }

    @DynamicPropertySource
    static void pointGithubClientAtLocalStub(DynamicPropertyRegistry registry) {
        githubStub = HttpServer.create(new InetSocketAddress(0), 0)
        githubStub.createContext("/", { exchange ->
            byte[] body = '[{"login":"someone","html_url":"https://github.com/someone","avatar_url":"https://github.com/someone.png","contributions":1}]'.bytes
            exchange.responseHeaders.add("Content-Type", "application/json")
            exchange.sendResponseHeaders(200, body.length)
            exchange.responseBody.withStream { it.write(body) }
        })
        githubStub.start()
        registry.add("github.api.apiUrl", { "http://localhost:${githubStub.address.port}" })
    }

    def cleanupSpec() {
        githubStub?.stop(0)
    }

    @Unroll
    def "Should serve #path over HTTP"() {
        when:
        ResponseEntity<String> response = restTemplate.getForEntity(path, String)

        then:
        response.statusCode == OK

        and:
        response.body?.length() > 0

        where:
        path << [
            "/",
            "/blogs",
            "/company-blogs",
            "/podcasts",
            "/presentations",
            "/about",
            "/all-issues",
            "/top-articles",
            "/rss",
            "/jvm-poland-slack",
            "/login",
            "/pl/rss",
            "/pl/rss.xml",
            "/pl/rss.json",
            "/pl/issues-rss"
        ]
    }

    def "Should serve /contributors through the Jersey based GitHub client"() {
        when: "the page is rendered, which performs a real request via the Jersey client"
        ResponseEntity<String> response = restTemplate.getForEntity("/contributors", String)

        then: "a Jersey version mismatch would surface here as a 500, never at compile time"
        response.statusCode == OK

        and:
        response.body.contains("someone")
    }

    @Unroll
    def "Should serve webjar resource #path referenced from page markup"() {
        when:
        ResponseEntity<String> response = restTemplate.getForEntity(path, String)

        then:
        response.statusCode == OK

        where:
        path << [
            "/webjars/jquery/jquery.min.js",
            "/webjars/bootstrap/js/bootstrap.min.js",
            "/webjars/bootstrap/css/bootstrap.min.css",
            "/webjars/metisMenu/metisMenu.min.css",
            "/webjars/metisMenu/metisMenu.min.js",
            "/webjars/startbootstrap-sb-admin-2/css/sb-admin-2.min.css",
            "/webjars/startbootstrap-sb-admin-2/js/sb-admin-2.min.js",
            "/webjars/font-awesome/css/font-awesome.min.css",
            "/webjars/html5shiv/html5shiv.min.js",
            "/webjars/respond/dest/respond.min.js"
        ]
    }
}
