# 🎨 RentStudio - RESTful API with Spring Boot

> ⚠️ This is a **copy of the backend** part of the FullStack project [RentStudio](https://github.com/RentStudioDH), originally located at [RentStudioDH on GitHub](https://github.com/RentStudioDH).

---

👋 **Welcome to the RESTful API built with Java and Spring Boot!** Below you'll find the requirements and instructions to run the project.

## 📋 Requirements

- ✅ **No manual installation of dependencies required**
- 🐳 **Docker must be installed and running on your local machine**
  - 🪟 [Docker Desktop for Windows](https://docs.docker.com/desktop/install/windows-install/)
  - 🍎 [Docker Desktop for MacOS](https://docs.docker.com/desktop/install/mac-install/)
  - 🐧 [Docker Engine for Linux](https://docs.docker.com/engine/install/)
- 💻 **IntelliJ IDEA Community/Pro**
- 📮 **Postman for testing endpoints**
- ☁️ **AWS account with IAM credentials (access key, secret key)**
  - 🔒 Make sure the credentials have **full S3 access only** (for security, use a credential dedicated to S3).

## ⚙️ Project Setup for Development

1. Navigate to the `env` folder at the project root, then go to the `dev` folder.
2. Duplicate the `example.dev.env` file and rename it to `.dev.env` (with the dot at the beginning).
3. Update the contents of `.dev.env` with your IAM-generated AWS keys.

## 🚀 Running the Project

### 🧪 In Development

- Ensure Docker is up and running.
- Run the main class `DigitalHouseApplication` or use IntelliJ IDEA:
  1. Open the project in IntelliJ IDEA.
  2. Go to the `RUN` section and select the `Digital House API` configuration as shown in the image below:

![image](https://github.com/1774-ProyectoIntegrador/backend/assets/29287036/19385d9e-c542-474b-947b-6aabbe0652e4)

## 💡 Additional Notes

- Make sure Docker Desktop is running before executing any scripts or using IntelliJ IDEA.
- Only modify `.env` files.  
  ⚠️ **Do not modify Docker Compose files** to avoid breaking the project.

## 🚧 Production Setup, Requirements and Deployment

The setup, requirements, and instructions for running the project in production are **not yet available**.


## 🎥 API Demo in action  

![RentStudioDemo](docs/RentStudioBackend.gif.gif)  

*(Example of the use of API endpoints added gif demo)*  
