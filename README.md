# debezium-kafka-spring-cdc
# Event-Driven Microservice with Spring Boot, Kafka, and PostgreSQL

A robust template for building event-driven microservices. This project demonstrates how to integrate a PostgreSQL database with an Apache Kafka messaging system using Spring Boot. It serves as a foundation for applications that react to data changes or business events in real-time.

---

## Core Technologies

- **Java 17**
- **Spring Boot 3.2.5**
- **Spring Kafka**: For producing and consuming messages.
- **Spring Data JPA**: For database interaction.
- **Spring Web**: For exposing RESTful APIs.
- **PostgreSQL**: As the relational database.
- **Undertow**: As the high-performance embedded web server.
- **Maven**: For dependency management and build automation.
- **Lombok**: To reduce boilerplate code.

---

## Prerequisites

Before you begin, ensure you have the following installed:
- JDK 17 or a later version.
- Apache Maven
- Docker and Docker Compose

---

## Getting Started

Follow these steps to get the project up and running on your local machine.

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd event-kafka
```

### 2. Set Up Dependencies with Docker

This project includes a `docker-compose.yml` file to easily spin up the required infrastructure (Kafka and PostgreSQL).

Start the services by running:
```bash
docker-compose up -d
```

This will start Kafka and PostgreSQL in the background.

### 3. Configure the Application

Create or update the `src/main/resources/application.properties` file with your database and Kafka connection details. The default values match the `docker-compose.yml` setup.

```properties
# src/main/resources/application.properties

# Spring Datasource Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase
spring.datasource.username=myuser
spring.datasource.password=mypassword
spring.jpa.hibernate.ddl-auto=update # Use 'validate' in production

# Kafka Configuration
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=event-consumer-group
spring.kafka.consumer.auto-offset-reset=earliest

# Example topic name (can be externalized)
app.kafka.topic.events=my-events-topic
```

---

## Build and Run

You can build and run the application using Maven.

### Build the Project

```bash
mvn clean install
```

### Run the Application

```bash
mvn spring-boot:run
```

The application will start and connect to the PostgreSQL and Kafka instances running in Docker.

---

## Contributing

Contributions are welcome! Please feel free to submit a pull request.

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a pull request.

