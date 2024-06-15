#!/bin/bash

# Start npm in background
cd meditrade-app
npm start &

# Go back to root directory
cd ..

# Start Spring Boot
mvn spring-boot:run
