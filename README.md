# RentStudio - RESTful API with Spring Boot

👋 ¡Bienvenido a la API RESTful creada en Java con Spring Boot! Aquí encontrarás los requisitos y las instrucciones para ejecutar el proyecto.

## Requerimientos

- **No requiere instalación manual de dependencias**
- **Docker instalado en tu máquina local y funcionando en todo momento**
  - Docker Desktop para [Windows](https://docs.docker.com/desktop/install/windows-install/)
  - Docker Desktop para [MacOS](https://docs.docker.com/desktop/install/mac-install/)
  - Docker Engine para [Linux](https://docs.docker.com/engine/install/)
- **IntelliJ IDEA Community/Pro**
- **Postman o Insomnia para pruebas de Endpoints**
- **Cuenta en AWS con credenciales IAM (access key, secret key)**
  - Debes tener permisos completos de S3 solamente (por seguridad, usa una credencial solo para S3).

## Configuración del Proyecto para Desarrollo

1. Ir a la carpeta `env` dentro de la raíz del proyecto, luego a la carpeta `dev` y duplicar `example.dev.env`. Renombrarlo a `.dev.env` con un punto al inicio tal cual.
2. Cambiar el contenido de `.dev.env` con tus claves de AWS creadas con IAM.

## Cómo correr el proyecto

### En Desarrollo

- Asegúrate de tener Docker en funcionamiento.
- Corre el archivo principal `DigitalHouseApplication` o usa IntelliJ IDEA:
  1. Abre el proyecto en IntelliJ IDEA.
  2. Ve a la parte de `RUN` y selecciona la opción `Digital House API` como se muestra en la imagen a continuación.
     
![image](https://github.com/1774-ProyectoIntegrador/backend/assets/29287036/19385d9e-c542-474b-947b-6aabbe0652e4)

## Notas Adicionales

- Asegúrate de tener Docker Desktop corriendo antes de ejecutar los scripts o usar IntelliJ IDEA.
- Solo modifica los archivos de `.env `, no deberá modificar ningun archivo de Docker Compose para no romper el proyecto.

## Configuración, Requisitos y Ejecución en Producción

La configuración, los requisitos y cómo correr el proyecto en producción todavía no estarán disponibles.
