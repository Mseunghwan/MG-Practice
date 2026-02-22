# 1. 빌드 스테이지 (소스 코드를 가져와서 JAR 파일로 빌드)
FROM eclipse-temurin:21-jdk-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /build

# Gradle 설정 파일과 래퍼(Wrapper)를 먼저 복사 (의존성 캐싱 목적)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 윈도우 환경에서 작성된 스크립트의 줄바꿈 문자 오류 방지 및 실행 권한 부여
RUN dos2unix ./gradlew || true
RUN chmod +x ./gradlew

# 의존성 패키지 미리 다운로드 (소스 코드가 바뀌어도 패키지는 캐시를 재사용하여 속도 향상)
RUN ./gradlew dependencies --no-daemon

# 실제 소스 코드 복사
COPY src src

# 테스트를 제외하고 프로젝트 빌드 (JAR 파일 생성)
RUN ./gradlew clean build -x test --no-daemon

# ---------------------------------------------------------

# 2. 실행 스테이지 (빌드된 JAR 파일만 가져와서 가볍게 실행)
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# 빌드 스테이지(builder)에서 완성된 JAR 파일만 복사
COPY --from=builder /build/build/libs/*-SNAPSHOT.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]