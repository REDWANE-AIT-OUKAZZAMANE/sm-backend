# smwall Backend Project Documentation :

This is a social media wall web app that displays posts from various social media platforms in real-time.

This documentation was created to demonstrate the **SMWALL** backend application built with Spring boot.

## Table of contents :

1. Features
2. Library/dependencies used
3. Requirements
4. Project Setup
5. Running tests
6. Api Documentation
7. Contributors
8. References

## 1. Features :

- Displays posts from Twitter, Instagram, and other popular social media platforms if needed.

- Allows users to filter posts based on keywords or hashtags.
- Automatically updates with new posts in real-time.
- Supports multiple display layouts to suit your needs.
- Easy to customize and integrate into your website or application.

The **SMWALL** backend application serves one frontend using Rest API with the following features:

- Authentication/authorization using JWT
- media moderation pinning and hiding
- Announcement management
- wall settings management
- footer and header management

## 2. Libraries/dependencies used :

Beside the known java/spring dependencies that are present in all spring boot projects, here are the other library used
specifically in SMWALL backend project :

- The authentication and authorization process is achieved using **JWT**(access token) using Spring boot jsonwebtoken .

- For Storage, MongoDB, with flyway for database migration.
- For Api documentation, **Swagger Open Api** is used.
- For integration Tests, **spring cloud contracts** is implemented using **GROOVY** based contracts. (contracts can be
  found in the path "src/test/resources/contracts")
- Spring Cloud Starter OpenFeign: Simplifies building REST clients for microservices communication.

- Spring Websocket and Spring Messaging: For implementing real-time communication using WebSockets

For building and running the application you need :

- [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven 3](https://maven.apache.org)

## 4. Project Setup:

### Cloning the project :

```sh
HTTPS: git clone https://github.com/REDWANE-AIT-OUKAZZAMANE/sm-backend.git
```

### Setup Cloud MongoDB

To use a cloud-based MongoDB service, follow the steps below:

Register for a cloud MongoDB service and obtain the MongoDB URI provided by the service.

In your application.yml or application.properties, configure the MongoDB settings as follows:

```sh
spring:
  data:
    mongodb:
      uri: {your_mongodb_uri_here}
      database: {your database name here}
```

Replace your_mongodb_uri_here with the actual connection string provided by the cloud MongoDB service. The URI typically
includes authentication credentials, database name, and additional options.

#### Accessing MongoDB with MongoDB Compass

You can use MongoDB Compass or any other MongoDB client to interact with the database. Follow these steps to connect to
the MongoDB database:

1. Open MongoDB Compass.
2. Click on "New Connection" to create a new connection.
3. Fill in the connection details, including the connection string, database name (SM_wall), and any credentials if
   applicable. The connection string should match the URI specified in the application-local.yml file.

Once connected, you can explore and interact with the data stored in the MongoDB database.

### Configuration

The application's configuration is specified in the application.yml file, which contains environment-specific properties
and sensitive information. To run the backend locally, you will need to create a local configuration file with specific
variables.

Create a file named application-local.yml in the same directory as application.yml and add the following properties:

```sh
server:
  port: 8080 # Specify the port number for the backend application.


# Configure only the needed variables like API keys, keywords, channel IDs, hashtags, etc.
webhooks:
  meta:
    verify-token: YOUR_META_SUBSCRIPTION_VERIFY_TOKEN
    hashtags:
      - hashtag1: YOUR_HASHTAG1_ID
      - hashtag2: YOUR_HASHTAG2_ID
    user-id: YOUR_USER_ID
    username: YOUR_USERNAME
    app-secret: YOUR_APP_SECRET
....
  youtube:
    verify-token: YOUR_YOUTUBE_SUBSCRIPTION_VERIFY_TOKEN
...
    api-key: YOUR_YOUTUBE_API_KEY
    channel-id: YOUR_YOUTUBE_CHANNEL_ID
    keyword: YOUR_YOUTUBE_KEYWORD

text-filter:
  perspective-api:
    apiKey: YOUR_PERSPECTIVE_API_KEY

# Add any additional properties you need for your application.
```

Replace the placeholders (YOUR_...) with your actual values for the MongoDB connection, API keys, hashtags, etc.

### Building the project :

```sh
cd backend
mvn clean install
```

### Run the project :

Run the BACKEND via command line

```shell
java -jar target/sm-backend-0.0.1-SNAPSHOT.jar
```

Run the BACKEND using maven commands

```shell
mvn spring-boot:run
```

Note that “-0.0.1-SNAPSHOT” represents the version of project, and it will upgrade by the time, so you should use the
‘sm-backend-{Version}.jar’ correspond to your actual checkout.

## 5. Running tests :

To run the tests, execute the command below on project main directory :

```shell
mvn clean compile test
```

## 6. Api Documentation :

You can access the application resource documentation if you copy past the URL below into your browser (only available
in dev and qa environment)

- http://localhost:8080/api/swagger-ui.html

## 7. Contributors :
- Redwan Ait Oukazzamane (FullStack Developer)

## 8. References :

### Reference Documentation

For further reference, please consider the following sections:

- [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
- [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/maven-plugin/reference/html/)
- [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/htmlsingle/#using-boot-devtools)
- [Spring Web](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
- [Spring Security](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/htmlsingle/#boot-features-security)
- [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.3.0.RELEASE/reference/htmlsingle/#boot-features-jpa-and-spring-data)
- [Swagger](https://swagger.io/) : Swagger is an API that help document HTTP resources.
- [Flyway Database Migration](https://www.baeldung.com/database-migrations-with-flyway)
- [Lombok](https://projectlombok.org/features/all)
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
