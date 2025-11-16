# user-service/Dockerfile
FROM eclipse-temurin:21-jre-alpine

# Устанавливаем зависимости (если нужно)
RUN apk add --no-cache bash

# Создаем пользователя для приложения
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем JAR-файл (он будет собран на этапе build)
COPY target/*.jar app.jar

# Меняем владельца
RUN chown appuser:appuser app.jar

# Переключаемся на пользователя
USER appuser

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]