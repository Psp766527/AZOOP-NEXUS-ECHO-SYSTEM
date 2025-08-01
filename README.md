# AZOOP SOFTWARE INFO SYSTEM 🚛🌐

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring--Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![PostgresSQL](https://img.shields.io/badge/Database-PostgreSQL-blue.svg)](https://www.postgresql.org/)
[![CI](https://github.com/your-org/dsc-nextgen/actions/workflows/ci.yml/badge.svg)](https://github.com/your-org/dsc-nextgen/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

  
**Developed By**: AZOOP SOFTWARE INFO SYSTEM

**Last Updated**: July 09, 2025

> A cloud-native microservices ecosystem for Digital Service Centre at Daimler Trucks Asia.
> A scalable, modular platform designed for seamless service discovery,
> error localization, gateway routing, and system monitoring in enterprise-grade
> digital infrastructures.

---

## 📦 Project Modules

### 1. **dsc-service-registry** (Eureka Service Registry)
- 📍 **Role**: Centralized Service Discovery using Spring Cloud Eureka.
- 📦 `spring-cloud-starter-netflix-eureka-server`
- 🌐 Access: `http://<host>:<port>/eureka/`
- 🔁 Supports high availability & replication.

---

### 2. **dsc-api-gateway** (API Gateway)
- 📍 **Role**: Reactive API Gateway using Spring Cloud Gateway.
- 🔀 Routes:
  - `/localization/graphql` → `localization` service
  - `/dsc/api/graphql/v1` → Dynamic Universal Router
- 🔐 Filter Chain:
  - Rate Limiting
  - Path Rewrite
  - Custom Dynamic Routing Filter

---

### 3. **dsc-system-health-monitor** (Monitoring / Health Core Service)
- 📍 **Role**: Provides shared health, metrics, and base services.
- 📦 Includes actuator endpoints, circuit breakers, and possible alerting hooks.
- 📈 the Monitors the health of all downstream services.

---

### 4. **dsc-locale-translation** (Error & Locale Translation)
- 📍 **Role**: Centralized i18n error message resolution engine.
- 🗣 Supports GraphQL interface for querying error metadata.
- 📘 Language fallback & resolution strategy integrated.

---

## 🐳 Docker Support

Each service is fully Dockerized:

```bash

docker build -t dsc-api-gateway ./dsc-api-gateway
docker build -t dsc-locale-translation ./dsc-locale-translation
docker build -t dsc-service-registry ./dsc-service-registry
docker build -t dsc-system-health-monitor ./dsc-system-health-monitor

```
---

## 📐 Project Architecture

```plaintext
                        +-------------------+
                        |  Client (FE/API)  |
                        +---------+---------+
                                  |
                        +---------v---------+
                        |  dsc-api-gateway  |       
                        |   (API Gateway)   <------------+
                        +---------^---------+            |
                                  |                      | 
                                  |                      |
+------------v------------+       V                      |
|dsc-service-registry     <------->                      |
|     (Eureka)            |                              |
+-------------------------+          +-------------------+ 
                                     |                    
                                     |
                            +--------+--+
                            |           |
             +--------------+           +------------+
             |                                       |
+------------v------------+              +------------v--------+
|dsc-locale-translation   |              |                     |
| (Error Locale Service)  |              |   Health Monitor    |
+-------------------------+              +---------------------+

```
## 📚 Tech Stack

- ✅ **Java 17**
  - Modern, robust, and performant JVM language used across all services.

- ✅ **Spring Boot**
  - Backend framework used for rapid microservice development.
  - Modules:
    - 🌀 **Reactive WebFlux**: Used in gateway and core modules for non-blocking I/O.
    - 🌐 **Spring Cloud Gateway**: For route management and filters.
    - 🧭 **Spring Cloud Eureka**: Service discovery for microservice registry.

- ✅ **MongoDB/PgSql**
  - NoSQL/PgSql database for flexible schema storage.
  - Used for error locale storage and dynamic configuration.

- ✅ **GraphQL (Spring GraphQL)**
  - Used in the localization module to serve i18n error messages and structured queries.

- ✅ **RESTful APIs**
  - Standard REST endpoints available across SentinelCore and supporting modules.

- ✅ **Docker**
  - Containerized deployment for each microservice.
  - Supports local and cloud-native environments (e.g., Kubernetes).

- ✅ **Micrometer + Spring Actuator**
  - Enables monitoring, health checks, and metrics exposure.
  - Integrates easily with Prometheus, Grafana, and other observability stacks.



# 📬 Contact

### Developer: Daimler Trucks Asia – Digital Software Center

### Email: 

### Organization: Daimler Truck Asia."# AZOOP-NEXUS-Sentinel-Big-Screen-Code" 

