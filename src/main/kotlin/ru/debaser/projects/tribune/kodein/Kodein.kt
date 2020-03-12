package ru.debaser.projects.tribune.kodein

import io.ktor.application.Application
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import ru.debaser.projects.tribune.route.RoutingV1

fun Kodein.MainBuilder.binds(app: Application) {
    bind<RoutingV1>() with eagerSingleton { RoutingV1() }
}