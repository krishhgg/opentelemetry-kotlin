package io.opentelemetry.kotlin.factory

import io.opentelemetry.kotlin.behavior.IdGeneratorBehavior
import io.opentelemetry.kotlin.error.SdkErrorHandler
import io.opentelemetry.kotlin.error.guardOrDefault

internal fun IdGeneratorBehavior.toIdGenerator(errorHandler: SdkErrorHandler): IdGenerator = when (this) {
    IdGeneratorBehavior.Random -> IdGeneratorImpl()
    is IdGeneratorBehavior.Custom -> errorHandler.guardOrDefault(
        IdGeneratorImpl(),
        "Failed to create IdGenerator, using default",
    ) {
        supplier()
    }
}
