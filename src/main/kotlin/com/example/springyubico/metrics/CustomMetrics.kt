package com.example.springyubico.metrics

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class CustomMetrics(
    private val meterRegistry: MeterRegistry
) {
    private lateinit var customCounter: Counter

    @PostConstruct
    fun init() {
        customCounter = Counter.builder("custom.counter")
            .description("A custom counter for demonstration purposes")
            .register(meterRegistry)
    }

    fun incrementCounter() {
        customCounter.increment()
    }
}
