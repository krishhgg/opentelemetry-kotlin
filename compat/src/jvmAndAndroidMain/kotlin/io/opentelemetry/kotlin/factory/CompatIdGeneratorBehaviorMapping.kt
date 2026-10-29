package io.opentelemetry.kotlin.factory

import io.opentelemetry.kotlin.behavior.IdGeneratorBehavior
import io.opentelemetry.kotlin.error.SdkErrorHandler
import io.opentelemetry.kotlin.error.guardOrDefault

internal fun IdGeneratorBehavior.toIdGenerator(errorHandler: SdkErrorHandler): IdGenerator = when (this) {
    IdGeneratorBehavior.Random -> CompatIdGenerator()
    is IdGeneratorBehavior.Custom -> errorHandler.guardOrDefault(
        CompatIdGenerator(),
        "Failed to create IdGenerator, using default",
    ) {
        supplier()
    }
}
