# Aviation Hexagonal

## Goal

Build a Scala multi module application from scratch using Hexagonal Architecture.

## Technical requirements

- Scala 3 latest LTS version
- Hexagonal architecture
- SBT 2 latest version Multi module project
- Functional programming using ZIO latest version library as functional effect
- Domain, Application, Infrasturcture layers
- Additional modules to separate technologies and avoid coupling
- Persistence via relational database, PostgreSQL
- Messageing via Kafka for Event Streamming

## Initial notes

a. Initial domain concepts:

Country, Airport, Airline, Route

b. Project example structure:

flight-network/
├── build.sbt
├── project/
│   └── Dependencies.scala
│
├── domain/                          # Pure business logic, zero framework deps
│   └── src/main/scala/domain/
│       ├── model/
│       │   ├── Country.scala
│       │   ├── Airport.scala
│       │   ├── Airline.scala
│       │   └── Route.scala
│       ├── error/
│       │   └── DomainError.scala
│       ├── service/                 # Domain services (pure logic, no I/O)
│       │   └── RouteValidator.scala
│       └── port/                    # Interfaces (the "hexagon" boundary)
│           ├── in/                  # Driving ports (use case interfaces)
│           │   ├── CreateRouteUseCase.scala
│           │   └── FindAirportUseCase.scala
│           └── out/                 # Driven ports (repository interfaces)
│               ├── AirportRepository.scala
│               ├── AirlineRepository.scala
│               └── RouteRepository.scala
│
├── application/                     # Use case orchestration, implements port.in
│   └── src/main/scala/application/
│       ├── service/
│       │   ├── CreateRouteService.scala
│       │   └── FindAirportService.scala
│       └── config/
│           └── ApplicationModule.scala   # Wiring (manual DI or wire macros)
│
├── infrastructure/                  # Implements port.out, adapters
│   ├── persistence/
│   │   └── src/main/scala/infrastructure/persistence/
│   │       ├── doobie/ (or slick)
│   │       │   ├── AirportRepositoryImpl.scala
│   │       │   ├── AirlineRepositoryImpl.scala
│   │       │   └── RouteRepositoryImpl.scala
│   │       └── mapper/
│   │           └── AirportRowMapper.scala
│   │
│   └── messaging/                   # optional: Kafka/Pulsar adapters
│       └── src/main/scala/infrastructure/messaging/
│           └── RouteCreatedPublisher.scala
│
├── adapter-http/                    # Driving adapter (REST/gRPC), implements port.in callers
│   └── src/main/scala/adapter/http/
│       ├── route/
│       │   ├── RouteController.scala
│       │   └── RouteDto.scala
│       └── error/
│           └── ErrorMapper.scala
│
├── bootstrap/                       # Composition root — the only module aware of everything
│   └── src/main/scala/bootstrap/
│       ├── Main.scala
│       └── WiringModule.scala
│
└── shared-kernel/                   # (optional) cross-cutting value objects/utils
    └── src/main/scala/shared/
        ├── Pagination.scala
        └── NonEmptyString.scala

infrastructure/
├── persistence-postgres/
│   └── src/main/scala/infrastructure/persistence/postgres/
│       ├── doobie/                          # or skunk/slick
│       │   ├── AirportRepositoryImpl.scala
│       │   ├── AirlineRepositoryImpl.scala
│       │   ├── RouteRepositoryImpl.scala
│       │   └── OutboxRepositoryImpl.scala   # writes events to outbox table
│       ├── mapper/
│       │   ├── AirportRowMapper.scala
│       │   └── RouteRowMapper.scala
│       └── config/
│           └── PostgresConfig.scala         # HikariCP / transactor setup
│
├── messaging-kafka/
│   └── src/main/scala/infrastructure/messaging/kafka/
│       ├── producer/
│       │   └── RouteEventProducer.scala     # implements port.out EventPublisher
│       ├── relay/
│       │   └── OutboxRelay.scala            # polls outbox table, publishes, marks sent
│       ├── consumer/                        # if you also consume events
│       │   └── AirportUpdatedConsumer.scala
│       ├── serde/
│       │   └── RouteEventCodec.scala        # circe/avro codecs
│       └── config/
│           └── KafkaConfig.scala
│
└── migration/
    └── src/main/resources/db/migration/
        ├── V1__create_airports.sql
        ├── V2__create_airlines.sql
        ├── V3__create_routes.sql
        └── V4__create_outbox.sql