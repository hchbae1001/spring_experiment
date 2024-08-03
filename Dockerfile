# 베이스 이미지로 OpenJDK 17을 사용합니다.
FROM openjdk:17-jdk-slim

# 애플리케이션 JAR 파일을 컨테이너에 복사합니다.
COPY build/libs/springExperiment-0.0.1-SNAPSHOT.jar /app/spring-experiment.jar

# 컨테이너의 작업 디렉토리를 설정합니다.
WORKDIR /app

# 애플리케이션을 실행합니다.
CMD ["java", "-jar", "spring-experiment.jar"]
