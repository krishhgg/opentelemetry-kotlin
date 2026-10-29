package io.opentelemetry.kotlin

import io.opentelemetry.kotlin.error.FakeSdkErrorHandler
import io.opentelemetry.kotlin.error.SdkErrorSeverity
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalApi::class)
internal class CreateCompatOpenTelemetryErrorHandlerTest {

    @Test
    fun `throwing config lambda is reported and earlier config is kept`() {
        val handler = FakeSdkErrorHandler()
        val api = createCompatOpenTelemetry {
            errorHandler(handler)
            propagator { w3cTraceContext() }
            error("boom")
        }

        assertEquals(listOf("traceparent", "tracestate"), api.propagator.fields().toList())
        api.tracerProvider.getTracer("test")
        val error = handler.userCodeErrors.single()
        assertEquals("OpenTelemetry configuration failed", error.message)
        assertEquals("boom", error.cause.message)
        assertEquals(SdkErrorSeverity.WARNING, error.severity)
        assertEquals(0, handler.sdkCodeErrors.size)
    }
}
