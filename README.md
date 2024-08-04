# MemberService

## Introduction
`MemberService` is a service that provides member management functionality. Given its various dependencies, I adopted the Facade pattern to efficiently manage these dependencies. This approach ensures a clear separation of roles across layers, enhancing maintainability and scalability.

## Architecture

### 1. Repository
- **Role**: Simple data retrieval and manipulation
- **Description**: Directly interacts with the database to retrieve and manipulate data.

### 2. Service
- **Role**: Exception handling and business logic execution
- **Description**: Handles necessary exception processing before executing business logic in collaboration with the Facade.

### 3. Facade
- **Role**: Business logic
- **Description**: Integrates multiple services and dependencies to handle core business logic. Manages various dependencies like `RedisUtil`, `AuthService`, and simplifies complex business processes.

### 4. Controller
- **Role**: DTO transformation and request/response handling
- **Description**: Receives client requests, transforms them into DTOs, and transforms the results from the service layer back into DTOs for response.

## Key Dependencies
- **RedisUtil**: Data caching and management using Redis
- **AuthService**: Authentication and authorization management
- Other various services and utility classes

## Usage

1. **Clone the Project**
    ```sh
    git clone <repository-url>
    cd <repository-directory>
    ```

2. **Set Up Environment**
   Configure the required environment variables in the settings file or as environment variables.

3. **Install Dependencies**
    ```sh
    ./gradlew build -x test
    ```

4. **Run the Application**
    ```sh
    ./gradlew bootRun
    ```

5. **Configure Application Properties**
   In the `src/main/resources` directory, find the `applicationPropertiesTemplate.properties` file and fill in the blank values as needed. Rename this file to `application.properties` to use it in your application.

    ```properties
    spring.application.name=

    # MySQL
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    # DB Source URL
    spring.datasource.url=
    # DB username
    spring.datasource.username=
    # DB password
    spring.datasource.password=
    spring.jpa.show-sql=true
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.properties.hibernate.format_sql=true
    spring.thymeleaf.cache=false
    spring.jpa.properties.hibernate.jdbc.time_zone=Asia/Seoul

    spring.data.redis.port=
    spring.data.redis.host=
    spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

    spring.elasticsearch.uris=
    spring.elasticsearch.username=
    spring.elasticsearch.password=

    logging.level.org.springframework.data.elasticsearch.client.WIRE=TRACE
    jwt.accessSecret=
    jwt.refreshSecret=
    ```

## Dependency
must start redis before run spring application

## Contribution

1. Fork the project (https://github.com/hchbae1001/spring-experiment/fork).
2. Create a new branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Create a pull request.

## License
[MIT](LICENSE)

## Author
Author: [hchbae1001](https://github.com/hchbae1001)

## Contact
- Email: hchbae1001@gmail.com
- GitHub: https://github.com/hchbae1001

## Logging

Upon successful API request, the logs will appear as follows:
```
2024-08-04T15:45:11.304+09:00 INFO 14864 --- [springExperiment] [nio-8080-exec-3] b.springexperiment.config.LoggingAspect : ======= [80c11d35-103e-40aa-9b12-6ac912ce16a2] =======
2024-08-04T15:45:11.304+09:00 INFO 14864 --- [springExperiment] [nio-8080-exec-3] b.springexperiment.config.LoggingAspect : [80c11d35-103e-40aa-9b12-6ac912ce16a2] Executing: findById | Args: [SecurityContextHolderAwareRequestWrapper[ org.springframework.security.web.header.HeaderWriterFilter$HeaderWriterRequest@1057ef29], 347]
2024-08-04T15:45:11.304+09:00 INFO 14864 --- [springExperiment] [nio-8080-exec-3] b.s.member.MemberController : Received request to find member by ID: 347 | URI: /api/v1/member/347
2024-08-04T15:45:11.368+09:00 INFO 14864 --- [springExperiment] [nio-8080-exec-3] b.s.member.MemberFacadeV1 : Member found in cache. Member ID: 347
2024-08-04T15:45:11.413+09:00 INFO 14864 --- [springExperiment] [nio-8080-exec-3] b.s.member.MemberController : Successfully found member by ID: 347 | URI: /api/v1/member/347
2024-08-04T15:45:11.414+09:00 INFO 14864 --- [springExperiment] [nio-8080-exec-3] b.springexperiment.config.LoggingAspect : [80c11d35-103e-40aa-9b12-6ac912ce16a2] Completed: findById in 110 ms | Result: <200 OK OK,CommonResponse[status=200, success=true, data=MemberCommonResponse[member_id=347, email=tester, name=alpha, nickname=나무늘보, phone=01033331111, created_at=2024-08-03T21:24:44, updated_at=2024-08-03T23:46:35]],[]>
2024-08-04T15:45:11.416+09:00 INFO 14864 --- [springExperiment] [nio-8080-exec-3] b.springexperiment.config.LoggingAspect : ======= [80c11d35-103e-40aa-9b12-6ac912ce16a2] =======
```
