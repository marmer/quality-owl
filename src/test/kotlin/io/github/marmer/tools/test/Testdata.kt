package io.github.marmer.tools.test

import io.github.marmer.tools.adapter.sonar.SonarClient
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.jeasy.random.FieldPredicates.inClass
import org.jeasy.random.FieldPredicates.named
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors
import kotlin.reflect.KClass


class Testdata(testClass: KClass<*>) {
    private var easyRandomForRandomizer = EasyRandom(EasyRandomParameters().seed(seedBy(testClass)))

    private val parameters = EasyRandomParameters()
        .seed(seedBy(testClass))
        .charset(StandardCharsets.UTF_8)
        .scanClasspathForConcreteTypes(true)
        .ignoreRandomizationErrors(true)
        .collectionSizeRange(1, 2)
        .randomize(
            named("value")
                .and(
                    inClass(SonarClient.ComponentMetricsResponseDTO.Component.Measure::class.java)
                )
        ) { easyRandomForRandomizer.nextInt().toString() }


    private var easyRandom = EasyRandom(parameters)

    private fun seedBy(context: KClass<*>) =
        context.qualifiedName.hashCode().toLong()


    fun <T> next(type: Class<T>): T =
        easyRandom.nextObject(type)


    fun <T : Any> next(type: KClass<T>): T =
        easyRandom.nextObject(type.java)

    fun <T> next(type: Class<T>, count: Int): List<T> =
        easyRandom.objects(type, count).collect(Collectors.toList())

    fun <T : Any> next(type: KClass<T>, count: Int): List<T> =
        easyRandom.objects(type.java, count).collect(Collectors.toList())


}
