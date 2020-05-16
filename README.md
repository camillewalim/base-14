# Technology Stack
 - Java 14 : Records, Modules, Immutable Collections
 - Spring Boot 2.2.4 (2020.03)
 - Webflux (Project Reactor)
 - Web sockets (Netty)
 - Swagger 3.0 (2020.03)
 - Flyway, Hibernate, H2 (and console server)
 - QuickTheories, StepVerifier

# Objective
Have a 'rolling' base image that let me achieve those objectives in parallel :
 - State-Of-The-Art Web back-end
 - Purely functional application both from code (webflux) & test (quicktheory, stepverifier)
 - Non-Blocking and Fast code (WebFlux + Netty websockets)
 - Easily embeddable (Swagger, Flyway, H2, Hibernate) for fully automated cicd (Jenkins & Docker)

