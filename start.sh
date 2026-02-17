#!/bin/bash
# Script para iniciar HOMA backend (con verificación de puerto)

echo "🏠 Iniciando Backend HOMA..."

# Verificar si el puerto 8080 está en uso
PID=$(lsof -ti:8080)
if [ ! -z "$PID" ]; then
    echo "⚠️  Puerto 8080 ocupado por proceso $PID. Deteniendo..."
    kill -9 $PID
    sleep 2
    echo "✅ Puerto liberado"
fi

# Iniciar backend
echo "🚀 Iniciando Spring Boot..."
./gradlew bootRun --args='--spring.profiles.active=local'
