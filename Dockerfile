# ================================
# Etapa 1: builder (Maven + JDK17)
# ================================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiamos solo pom.xml primero (aprovechar caché de dependencias)
COPY pom.xml .

RUN mvn -B dependency:go-offline

# Ahora copiamos el código fuente
COPY src ./src

# Construimos el proyecto (saltando tests)
RUN mvn -B clean package -DskipTests


# ================================
# Etapa 2: runtime (imagen ligera)
# ================================
FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

# Copiamos el jar generado desde la etapa builder
COPY --from=builder /app/target/*.jar app.jar


# Comando de arranque
ENTRYPOINT ["java", "-jar", "app.jar"]
