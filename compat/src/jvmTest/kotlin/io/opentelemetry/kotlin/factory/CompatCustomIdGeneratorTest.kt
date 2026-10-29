package io.opentelemetry.kotlin.factory

import io.opentelemetry.kotlin.ExperimentalApi
import io.opentelemetry.kotlin.OpenTelemetrySdk
import io.opentelemetry.kotlin.createCompatOpenTelemetry
import io.opentelemetry.kotlin.error.FakeSdkErrorHandler
import io.opentelemetry.kotlin.tracing.export.FakeSpanProcessor
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalApi::class)
internal class CompatCustomIdGeneratorTest {

    private val traceId = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    private val spanId = "bbbbbbbbbbbbbbbb"

    /** A plain Kotlin [IdGenerator] that does NOT implement [OtelJavaIdGenerator]. */
    private val custom = object : IdGenerator {
        override fun generateSpanIdBytes(): ByteArray = spanId.hexToByteArray()
        override fun generateTraceIdBytes(): ByteArray = traceId.hexToByteArray()
        override val invalidTraceId: ByteArray = "00000000000000000000000000000000".hexToByteArray()
        override val invalidSpanId: ByteArray = "0000000000000000".hexToByteArray()
    }

    @Test
    fun `custom kotlin id generator is honored via the java adapter`() {
        val sdk = createCompatOpenTelemetry {
            idGenerator { custom }
            tracerProvider { export { FakeSpanProcessor() } }
        }
        val span = sdk.tracerProvider.getTracer("test").startSpan("span")
        assertEquals(traceId, span.spanContext.traceId)
        assertEquals(spanId, span.spanContext.spanId)
    }

    @Test
    fun `throwing custom id generator is reported and span creation still works`() {
        val handler = FakeSdkErrorHandler()
        val sdk = createCompatOpenTelemetry {
            errorHandler(handler)
            idGenerator { error("boom") }
            tracerProvider { export { FakeSpanProcessor() } }
        } as OpenTelemetrySdk

        assertIs<CompatIdGenerator>(sdk.idGenerator)
        val span = sdk.tracerProvider.getTracer("test").startSpan("span")
        assertTrue(span.spanContext.isValid)

        val error = handler.userCodeErrors.single()
        assertEquals("Failed to create IdGenerator, using default", error.message)
        assertEquals("boom", error.cause.message)
        assertEquals(0, handler.sdkCodeErrors.size)
    }
}
