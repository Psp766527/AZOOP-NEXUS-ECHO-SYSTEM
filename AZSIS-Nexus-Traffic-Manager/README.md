# DSC-API-Gateway (API GateWay)

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring--Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![PostgresSQL](https://img.shields.io/badge/Database-PostgreSQL-blue.svg)](https://www.postgresql.org/)
[![CI](https://github.com/your-org/dsc-nextgen/actions/workflows/ci.yml/badge.svg)](https://github.com/your-org/dsc-nextgen/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)


**Version**: `DSC-API-Gateway_V1.0`  

**Developed By**: Daimler Trucks Asia – Digital Software Center

**Last Updated**: July 15, 2025

---

## Overview

DSC-API-Gateway is a robust Spring Cloud Gateway designed to **route**, **secure**, and **monitor** microservice
traffic within the DSC ecosystem. It serves as the **entry point** for all API requests, offering advanced features
like:

- ✅ Dynamic routing
- 🚦 Custom rate limiting (PgSQL + Bucket4j)
- 🔁 Retry policies
- ⚡ Reactive architecture
- 🪵 Integrated logging and debugging

---

# Technical and Functional Explanation

The DSC-API-Gateway operates as a reactive API Gateway built on Spring Cloud Gateway, designed to enhance the
scalability and resilience of microservice architectures. Below is a detailed breakdown of its functionality and
technical underpinnings:

- **Communication with DSC Service Registry Server (Eureka) for Service Discovery**:
  The gateway integrates with the **DSC Service Registry Server**, an Eureka-based service registry, to dynamically discover available
  microservices. Upon startup, it registers itself with the Eureka Server and periodically synchronizes with the registry
  to fetch the latest service instances (e.g., IP addresses and ports). This process relies on the
  `spring-cloud-starter-netflix-eureka-client` dependency, which enables the gateway to query the Eureka Server at
  `http://localhost:8761/eureka/` for service metadata. Post-discovery, the gateway maps logical service IDs (e.g.,
  `lb://localization`) to physical endpoints, ensuring requests are routed to healthy instances based on Eureka’s health
  checks.

- **Dynamic Routing to Microservices (e.g., Localization Service)**:
  Leveraging Spring Cloud Gateway’s routing capabilities, the gateway dynamically routes incoming HTTP requests to
  appropriate microservices based on path predicates. For instance, a request to `/localization/graphql` is matched using a
  `Path=/localization/graphql` predicate and forwarded to the localization service, resolved via the Eureka Server as `lb://localization`.
  The `RewritePath` filter transforms the URL (e.g., `/localization/graphql` to `/graphql`) to align with the target service’s
  endpoint (`http://localhost:8085/graphql`). This feature ensures flexibility, allowing the addition of new
  microservices without gateway reconfiguration.

- **Custom Rate Limiting with PgSQL-backed Configuration using Bucket4j**:
  The gateway implements a custom rate-limiting mechanism using the **Bucket4j** library, with configurations stored in
  a MongoDB collection (`rate_limit_config`). Each rate limit policy is defined by fields such as `requestsPerMinute`,
  `timeWindow`, `timeUnit`, and `burstCapacity`, persisted as JSON documents. The `RateLimiterFilter` queries MongoDB at
  runtime to instantiate `Bucket` objects, enforcing limits (e.g., 100 requests per minute with a 10-request burst).
  This approach provides persistence and scalability, allowing administrators to update limits centrally without
  redeploying the gateway.

- **Support for Retries, Burst Capacity, and Priority-based Limits**:
  The gateway enhances resilience with a retry policy configurable via `customAttributes` (e.g., `maxRetries: 2`,
  `retryDelay: 500ms`), enabling clients to retry requests after rate limit exceedances. **Burst capacity** allows
  temporary spikes beyond the base limit (e.g., 10 extra requests), managed by Bucket4j’s bandwidth configuration. *
  *Priority-based limits** leverage the `priority` field (e.g., 1 for high-priority clients) to adjust rate limits
  dynamically, ensuring fair resource allocation across clients.

- **Reactive Architecture with Spring WebFlux**:
  Built on **Spring WebFlux**, the gateway adopts a non-blocking, event-driven architecture using the reactive Netty
  server. This enables handling thousands of concurrent connections efficiently, with the
  `web-application-type: reactive` setting in `application.yaml`. The `RateLimiterFilter` integrates with WebFlux’s
  reactive chain, using `Mono.defer` for asynchronous retry logic, ensuring high throughput and low latency even under
  load.

- **Integrated Logging and Debugging**:
  The gateway incorporates **SLF4J** with DEBUG-level logging (e.g., `org.springframework.cloud.gateway: DEBUG`) to
  provide detailed insights into routing, filtering, and rate-limiting decisions. Configured in `application.yaml`, logs
  capture filter invocations (e.g., `RateLimiterFilter invoked`) and errors, aiding developers in troubleshooting. The
  `reactor.netty.http.client` and `reactor.netty.http.server` log levels further expose network-level details, enhancing
  debuggability.

---

## 🧠 Architecture Overview

The DSC-API-Gateway operates as a reactive API Gateway, orchestrating communication between the **Eureka Server** (
Eureka-based service registry) and downstream microservices like the localization service. Below is a textual description of
the flow, followed by a placeholder for an image-based flow diagram:

### Flow Description

1. **Service Discovery**: The gateway registers with the Eureka Server at `http://localhost:8761/eureka/` and fetches
   service metadata (e.g., instances, status) using the `spring-cloud-starter-netflix-eureka-client`.
2. **Request Routing**: Incoming requests (e.g., `/localization/graphql`) are matched via path predicates and routed to the
   localization service (`lb://localization`), resolved to `http://localhost:8085/graphql` by the Eureka Server.
3. **Rate Limiting & Processing**: The `RateLimiterFilter` applies custom limits (e.g., 100 requests/min) using
   Bucket4j, with configurations from MongoDB, before forwarding the request.
4. **Response**: The localization service processes the request (e.g., GraphQL query) and returns a response via the gateway.

### Flow Diagram

- **Eureka Server (DSC Service Registry Server)**: Manages service registration/discovery
- **DSC-API-Gateway**: Handles routing, rate-limiting, and rewriting
- **Client Service**: GraphQL backend service (`http://localhost:8085/graphql`)

---

## 🔧 Prerequisites

- **Java**: JDK 17+
- **Maven**: 3.6.0+
- **PgSQL**: Running at `localhost:defaultPort` (configurable)
- **IDE**: IntelliJ IDEA (recommended)

---

## 🚀 Setup Instructions

### Clone the Repository

```bash

git clone <repository-url>**
cd DSC-API-Gateway**
```

---

## Configure Environment

### - **Start MongoDB**: Update spring.data.mongodb.uri in application.yaml if using a different host/port.

### - **Install dependencies**: mvn clean install

## Import into IntelliJ

- **Open IntelliJ IDEA.**

- **Select File** > Open and choose **pom.xml.**

- **Wait for indexing and dependency resolution.**

---

## ⚙️ Configure application.yaml

### - Edit  **src/main/resources/application.yaml**:

### 📁 `application.yaml` Configuration

```yaml
server:
  port: ${SERVER_PORT:9088}
  address: ${SERVER_ADDRESS:0.0.0.0}

spring:
  main:
    web-application-type: reactive

  apigateway:
    kafka:
      bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:127.0.0.1:31092,127.0.0.1:31093,127.0.0.1:31094}
      consumer:
        group-id: ${KAFKA_CONSUMER_GROUP:local-apigateway-group}
        key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
        auto-offset-reset: ${KAFKA_AUTO_OFFSET_RESET:none}
        enable-auto-commit: ${KAFKA_ENABLE_AUTO_COMMIT:true}
        max-poll-records: ${KAFKA_MAX_POLL_RECORDS:500}
        topic: ${KAFKA_TOPIC:local-apigateway-dsc-tp}
      producer:
        key-serializer: org.apache.kafka.common.serialization.StringSerializer
        value-serializer: org.apache.kafka.common.serialization.StringSerializer
        acks: ${KAFKA_ACKS:all}
        retries: ${KAFKA_RETRIES:3}
        batch.size: ${KAFKA_BATCH_SIZE:16384}
        linger.ms: ${KAFKA_LINGER_MS:1}
        buffer.memory: ${KAFKA_BUFFER_MEMORY:33554432}
        retry.BackOffPeriod: ${KAFKA_BACKOFF:2000}
        retry.MaxRetryAttempts: ${KAFKA_MAX_RETRIES:4}

  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/dsc}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:dscpassword}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      idle-timeout: 30000
      max-lifetime: 60000

  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: ${JPA_HIBERNATE_DDL_AUTO:create-drop}
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        jdbc:
          lob:
            non_contextual_creation: true

  loadBalancerURI: lb://

  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: apigateway
          uri: lb://${ROUTE_SERVICE_ID:apigateway}
          predicates:
            - Path=/apigateway/graphql
          filters:
            - name: RateLimiterFilter
            - RewritePath=/apigateway/(?<segment>.*), /${segment}

        - id: universal-rest-graphql-router
          uri: lb://placeholder
          predicates:
            - Path=/**
          filters:
            - name: DynamicUniversalRoutingFilter
              enabled: false

dynamic-routing:
  self-service-id: ${DYNAMIC_SELF_SERVICE_ID:apigateway}
  gateway-prefix: /dsc/api/
  graphql:
    inbound-path: /dsc/api/graphql/v1
    service-path: /graphql
  rest:
    strip-prefix: true

  logging:
    level:
      org.springframework.cloud.gateway: DEBUG
      reactor.netty.http.client: DEBUG
      reactor.netty.http.server: DEBUG

eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_DEFAULT_ZONE:http://localhost:9087/eureka/}
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    instance-id: ${SPRING_APPLICATION_NAME:apigateway}:${SERVER_PORT:9088}

```

---

## ▶️ Run the Application

### - **Right-click: mainJavaFile.java and select Run.**

### - **Or use**:Maven CLI

```bash

mvn spring-boot:run
```

---

## Verify Setup

### Start the Eureka server at http://localhost:8761.

- **Ensure the localization service is registered with Eureka.**
- **Test the endpoint**:

```shell

curl --location 'http://localhost:8090/localization/graphql' \
--header 'X-Client-Id: test-client' \
--header 'Content-Type: application/json' \
--data '{"query":"mutation CreateMsgTemplate {...}"}'
````

---

### - **Check IntelliJ console for DEBUG logs.**

## Configuration

### 📊 -**Rate Limiting**:

#### Managed in PgSQL (rate_limit_config collection). Example:

```json
{
  "clientId": "test-client",
  "route": "/localization/graphql",
  "requestsPerMinute": 100,
  "timeWindow": 60,
  "timeUnit": "SECONDS",
  "burstCapacity": 10,
  "priority": 1,
  "status": "ACTIVE",
  "customAttributes": {
    "maxRetries": "2",
    "retryDelay": "500"
  }
}
```

# 📦 Usage

#### 1- **Access the gateway at http://localhost:8090**

#### 2- **Use the X-Client-Id header for rate limiting.**

#### 3- **Monitor logs for rate limit enforcement and retries.**

# 📬 Contact

### Developer: Daimler Trucks Asia – Digital Software Center

### Email: 

### Organization: Daimler Truck Asia.


