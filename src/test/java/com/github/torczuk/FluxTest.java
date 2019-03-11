package com.github.torczuk;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.stream.Stream;

class FluxTest {

    static final BiFunction<HashMap<String, Integer>, Tuple2<String, Integer>, HashMap<String, Integer>> LETTER_COUNTER = (collected, actualLetter) -> {
        String letter = actualLetter.getT1();
        Integer count = actualLetter.getT2();
        collected.putIfAbsent(letter, 0);
        collected.put(letter, collected.get(letter) + count);
        return collected;
    };

    @Test
    void shouldMapWords() {
        Flux<String> words =
                Flux.fromArray("So so you think you can tell".split("\\s"))
                        .map(String::toLowerCase);

        StepVerifier
                .create(words)
                .expectNext("so", "so", "you", "think", "you", "can", "tell")
                .verifyComplete();
    }

    @Test
    void shouldSelectDistinctLetters() {
        Flux<String> words =
                Flux.fromArray("So so you think you can tell".split("\\s"))
                        .map(String::toLowerCase)
                        .flatMap(word -> Flux.fromArray(word.split("")))
                        .distinct();

        StepVerifier
                .create(words)
                .expectNext("s", "o", "y", "u", "t", "h", "i", "n", "k", "c", "a", "e", "l")
                .verifyComplete();
    }

    @Test
    void shouldOrderDistinctLetters() {
        Flux<String> words =
                Flux.fromArray("So so you".split("\\s"))
                        .map(String::toLowerCase)
                        .flatMap(word -> Flux.fromArray(word.split("")))
                        .sort()
                        .distinct();

        StepVerifier
                .create(words)
                .expectNext("o", "s", "u", "y")
                .verifyComplete();
    }

    @Test
    void shouldCountLetters() throws InterruptedException {
        Flux<Integer> one = Flux.fromStream(Stream.generate(() -> 1));

        Flux<Tuple2<String, Integer>> letters = Flux.fromArray("Hello".split("\\s"))
                .map(String::toLowerCase)
                .flatMap(word -> Flux.fromArray(word.split("")))
                .zipWith(one)
                .reduce(new HashMap<>(), LETTER_COUNTER)
                .flatMapMany(map -> Flux.fromStream(map.entrySet().stream().map(es -> Tuples.of(es.getKey(), es.getValue()))))
                .sort(Comparator.comparing(Tuple2::getT1));

        StepVerifier
                .create(letters)
                .expectNext(
                        Tuples.of("e", 1),
                        Tuples.of("h", 1),
                        Tuples.of("l", 2),
                        Tuples.of("o", 1)
                )
                .verifyComplete();
    }

    @Test
    void shouldConcatDelayed() {
        Flux<String> letters1 = Flux.just("a", "b").delaySequence(Duration.ofMillis(100L));
        Flux<String> letters2 = Flux.just("c", "d").delaySequence(Duration.ofMillis(1L));

        Flux<String> letters = Flux.concat(letters1, letters2);

        StepVerifier
                .create(letters)
                .expectNext("a", "b", "c", "d")
                .verifyComplete();

    }
}
