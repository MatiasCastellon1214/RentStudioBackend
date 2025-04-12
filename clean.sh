#!/bin/sh

# Esperar a que los contenedores estén levantados (puedes ajustar el tiempo de espera según tus necesidades)
sleep 60

# Eliminar imágenes colgantes (dangling)
docker image prune -f

# Eliminar volumes sin uso
docker volume prune -f

echo "Limpieza completada."