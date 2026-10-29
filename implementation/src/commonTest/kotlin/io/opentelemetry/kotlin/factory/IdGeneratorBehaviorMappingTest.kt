package io.opentelemetry.kotlin.factory

import io.opentelemetry.kotlin.behavior.IdGeneratorBehavior
import io.opentelemetry.kotlin.error.NoopSdkErrorHandler
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertSame

internal class IdGeneratorBehaviorMappingTest {

    @Test
    fun mapsRandomBehavior() {
        assertIs<IdGeneratorImpl>(IdGeneratorBehavior.Random.toIdGenerator(NoopSdkErrorHandler))
    }

    @Test
    fun mapsCustomBehavior() {
        val custom = FakeIdGenerator()

        assertSame(custom, IdGeneratorBehavior.Custom { custom }.toIdGenerator(NoopSdkErrorHandler))
    }
}
