# ===========================
# Stage 1: Build stage
# ===========================
FROM eclipse-temurin:17-jdk-alpine AS builder

# Thiết lập thư mục làm việc
WORKDIR /app

# Cài Maven
RUN apk add --no-cache maven

# Copy pom.xml trước (để tận dụng cache)
COPY pom.xml .

# Copy toàn bộ source code
COPY src ./src

# Build project (bỏ qua test)
RUN mvn clean package -DskipTests -Dfile.encoding=UTF-8

# ===========================
# Stage 2: Runtime stage
# ===========================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy jar từ stage builder
COPY --from=builder /app/target/*.jar app.jar

# Expose cổng mặc định Spring Boot
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]