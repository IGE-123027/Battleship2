# Usamos a imagem oficial do Maven com Java 21 (ambiente completo de desenvolvimento)
FROM maven:3.9.6-eclipse-temurin-21

# Instalar ferramentas extra que dão jeito para programar e usar git
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends git curl

# Definir a pasta de trabalho
WORKDIR /workspace