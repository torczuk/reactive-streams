package com.github.torczuk.reactivesandbox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ServerApp

fun main(args: Array<String>) {
	runApplication<ServerApp>(*args)
}
