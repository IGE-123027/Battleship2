FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY target/BattleshipGamePlayer-2.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-cp", "app.jar", "battleship.Main"]