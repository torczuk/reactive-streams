package com.github.torczuk.reactivesandbox

import com.github.torczuk.reactivesandbox.controller.OddNumberSupplier
import org.assertj.core.api.Assertions
import org.junit.Test
import java.util.stream.Collectors
import java.util.stream.Stream

class SupplierTest {

    @Test
    fun shouldGenerateOddNumbers() {
        val `5 first odd numberts` = Stream.generate(OddNumberSupplier())
                .limit(5)
                .collect(Collectors.toList())

        Assertions.assertThat(`5 first odd numberts`).isEqualTo(listOf(1, 3, 5, 7, 9))
    }
}
