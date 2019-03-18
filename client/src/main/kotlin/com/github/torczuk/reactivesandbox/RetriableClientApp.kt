package com.github.torczuk.reactivesandbox

import org.slf4j.LoggerFactory
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.web.reactive.function.client.WebClient
import reactor.retry.Retry
import java.net.ConnectException
import java.time.Duration
import java.time.LocalDateTime

@SpringBootApplication
class RetriableClientApp {

    val log = LoggerFactory.getLogger(RetriableClientApp::class.java)

    fun main(args: Array<String>) {
        val ctx = SpringApplicationBuilder(RetriableClientApp::class.java)
                .web(WebApplicationType.NONE)
                .application()
                .run(*args)

        log.info("Get webClient ...")
        val webClient = ctx.getBean("streamClient", WebClient::class.java)

        val retry = Retry
                .anyOf<ConnectException>(java.net.ConnectException::class.java)
                .randomBackoff(Duration.ofMillis(100), Duration.ofSeconds(60))
                .doOnRetry { log.warn("Retrying ${LocalDateTime.now()} ...") }

        webClient.get().uri("/numbers/odd")
                .exchange()
                .flatMapMany { r -> r.bodyToFlux(String::class.java) }
                .doOnError { e -> log.error("Error ${e::class.java}", e) }
                .retryWhen(retry)
                .delayElements(Duration.ofSeconds(1))
                .subscribe { println(it) }

        log.info("Start consuming  ...")

        Thread.currentThread().join()
    }
}
