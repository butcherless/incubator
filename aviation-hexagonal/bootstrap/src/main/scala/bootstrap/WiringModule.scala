package bootstrap

import application.service.*
import domain.port.in.*
import domain.port.out.*
import infrastructure.messaging.kafka.producer.RouteEventProducer
import infrastructure.persistence.postgres.config.PostgresConfig
import infrastructure.persistence.postgres.repository.*
import zio.*

object WiringModule {

  private val transactorLayer = PostgresConfig.transactorLayer

  private val repositoryLayer: TaskLayer[
    CountryRepository & AirportRepository & AirlineRepository & RouteRepository & OutboxRepository
  ] = transactorLayer >>> (
    DoobieCountryRepository.layer ++
      DoobieAirportRepository.layer ++
      DoobieAirlineRepository.layer ++
      DoobieRouteRepository.layer ++
      DoobieOutboxRepository.layer
  )

  private val useCaseLayer: URLayer[
    CountryRepository & AirportRepository & AirlineRepository & RouteRepository,
    FindCountryUseCase & FindAirportUseCase & FindAirlineUseCase & CreateRouteUseCase
  ] =
    FindCountryService.layer ++
      FindAirportService.layer ++
      FindAirlineService.layer ++
      CreateRouteService.layer

  private val messagingLayer: TaskLayer[EventPublisher] =
    RouteEventProducer.producerLayer >>> RouteEventProducer.layer

  // Exposes use cases + OutboxRepository (for relay) + EventPublisher (for relay)
  val appLayer: TaskLayer[
    FindCountryUseCase & FindAirportUseCase & FindAirlineUseCase & CreateRouteUseCase &
      OutboxRepository & EventPublisher
  ] =
    repositoryLayer >>> (useCaseLayer ++ ZLayer.environment[OutboxRepository]) ++
      messagingLayer
}
