# 🤖 mga-chat-bot-trainer-service

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)](https://spring.io/)
[![Java Version](https://img.shields.io/badge/Java-17-007396?logo=openjdk)](https://openjdk.org/)
[![Build Tool](https://img.shields.io/badge/Build-Apache%20Maven%203.9.x-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![AI Engine](https://img.shields.io/badge/AI%20Engine-Chat%20AI%20%2F%20LLM-008080?logo=openai)](https://spring.io/projects/spring-ai)

## 📌 Service Overview

`mga-chat-bot-trainer-service` provides AI-driven conversational workout guidance and motivational coaching for gym members within the MGA Members App.

It supports dual personality modes (a supportive mode for encouraging guidance versus an aggressive mode for high-intensity motivation) and automatically persists session interaction logs via internal RestClient calls down to `mga-records-crud-service`.

### 🏛️ Architectural Features
* Dynamic Personality Prompting: Manages conversational context switching between encouraging and high-intensity motivational personas.
* Background History Persistence: Automatically dispatches background conversation logs to `mga-records-crud-service` for long-term user history tracking.
* Standardized Data Contract: Imports shared domain models directly from `mga-all-models-jars`.

---

## 🛠️ Technical Specifications
* Port: 8086
* Language/Build: Java 17 | Apache Maven 3.9.x
* Runtime Container: Standard JVM Container
* Downstream Service Call: mga-records-crud-service (Port 8082)
* Shared Contract: mga-all-models-jars

---

## ⚙️ Build & Deployment Instructions
# Standard JVM Build & Run
mvn clean package
java -jar target/mga-chat-bot-trainer-service.jar

# Spring Boot Run
mvn spring-boot:run

```
