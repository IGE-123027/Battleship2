# Usar a imagem oficial do Java
FROM eclipse-temurin:21-jre-alpine

# Definir a pasta de trabalho dentro do contentor
WORKDIR /app

# Copiar APENAS o ficheiro executável correto gerado na tua máquina
COPY target/BattleshipGamePlayer-2.0.jar app.jar

# Comando de arranque do jogo
ENTRYPOINT ["java", "-jar", "app.jar"]
