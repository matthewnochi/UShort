# UShort

Ushort is a webapplication that can shorten inputed URLs (optionally with an inputted code). 
This project is containerized with Docker for easy local development and deployment.
This document explains how to run the UShort web application and its dependencies (Postgres database) locally. 

# Prerequisites

Before you begin, ensure you have the following installed:
* [Docker](https://www.docker.com/get-started)
* [Docker Compose](https://docs.docker.com/compose/install/)
* [pgAdmin4](https://www.pgadmin.org/) for database management (optional)

# Usage

### Starting Application

To run the application, follow these steps:
1. Clone this repository and change directory to UShort.
2. Build and start the application with Docker Compose:
```bash
docker-compose up --build
```
3. The application will now be accessible at [http://localhost:3000/](http://localhost:3000/), and the backend API can be viewed via Swagger at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).
4. To stop the application, press `Ctrl+C` in the terminal where the Docker Compose command is running.

### Postgres
The application uses PostgreSQL for data storage. By default, a PostgreSQL container will be made with Docker. You can connect to this database using the following credentials:
* Host name/address: localhost
* Port: 5433
* Maintenence Database: postgres
* Username: postgres
* Password: password
pgAdmin4 is recommended for easier database management and viewing.


