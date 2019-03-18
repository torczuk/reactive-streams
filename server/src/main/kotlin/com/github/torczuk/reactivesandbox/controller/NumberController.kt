package com.github.torczuk.reactivesandbox.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.util.concurrent.atomic.AtomicLong
import java.util.function.Supplier
import java.util.stream.Stream

@RestController
@RequestMapping("numbers")
class NumberController {
    val logger = LoggerFactory.getLogger(NumberController::class.java)

    @RequestMapping("/odd", produces = ["text/event-stream"])
    fun odd() = Flux.fromStream(Stream.generate(OddNumberSupplier()))

    @RequestMapping("/even", produces = ["text/event-stream"])
    fun even() = Flux.fromStream(Stream.generate(EvenNumberSupplier()))

}

class OddNumberSupplier : Supplier<Long> {
    private val actual = AtomicLong(1)
    override fun get() = actual.getAndUpdate { v -> v + 2 }
}

class EvenNumberSupplier : Supplier<Long> {
    private val actual = AtomicLong(0)
    override fun get() = actual.getAndUpdate { v -> v + 2 }
}