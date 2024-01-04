# Employee Service - Coding Challenge Implementation with Spring Boot 3 and Spring Security 6

The project includes the following functionalities:

- User Registration and Login with JWT Authentication
- Refresh Token stored in db
- Access Denied Handling
- Swagger Documentation of API's (Defined in Controller packages)
- Event Creations and Listeners using Apache Kafka Configuration
- Use of PostGres Sql to store data
- Passwords storage in Encrypted format (Bcrypt)

# Technologies

- Spring Boot 3.1.7 with Java 17
- Spring Security
- Spring Data JPA with Hibernate
- JSON Web Tokens(JWT)
- BCrypt
- Maven
- OpenAPI / Swagger Docs
- Lombok
- Apache Kafka (Producers and Consumers)
- Junit and Mockito

# Getting Started
To get started with this project, you will need to have the following installed on your local machine:
- JDK 17+
- Maven 3+
- Apache Kafka (Running in Docker Container or Locally)
- Postgres SQL (Running in Docker Container or Locally)

## Configure Spring Datasource, JPA, App properties
1. Clone the repository
2. Open src/main/resources/application.properties
```
#Database related variables
spring.datasource.url=jdbc:postgresql://localhost:5432/<schema-name>
spring.datasource.username=postgres
spring.datasource.password=<password>
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation= true
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

#Kafka Configurations
spring.kafka.consumer.bootstrap-servers=<hostname>:<port>
spring.kafka.producer.bootstrap-servers=<hostname>:<port>
```
## Build and run the Project
- Build the project: `mvn clean install`
- Run the project: `mvn spring-boot:run`

The application will be available at http://localhost:8080.

# Test project

User registration endpoint

`POST http://localhost:8080/api/v1/auth/register`

Then you may use Authentication url by passing username and password.

`POST http://localhost:8080/api/v1/auth/authenticate`

After that you can use any of the API's from Employee Section

You can also logout from the existing session. API's as below :

`POST http://localhost:8080/api/v1/auth/logout`

For detailed documentation and testing of the APIs, access the Swagger UI by visiting:
```
http://localhost:8080/swagger-ui.html
```
