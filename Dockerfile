# Java 21 JDK 이미지를 기반으로 사용
FROM eclipse-temurin:21-jdk-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 파일을 컨테이너 안으로 복사
COPY build/libs/MG-Practice-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]