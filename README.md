# Finance Application - Microservices Architecture

## Overview

This project is a comprehensive financial application built using a microservices architecture. The system consists of multiple independent services that handle various financial operations such as account management, billing, investment tracking, and transaction processing. Each service is designed to be scalable and maintainable, ensuring a robust and efficient application.

## Table of Contents

- [Features](#features)
- [Technologies](#technologies)
- [Microservices Overview](#microservices-overview)
- [Setup and Installation](#setup-and-installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Microservices Architecture**: Independent services for different functionalities.
- **Eureka Server**: Service discovery and registration for seamless inter-service communication.
- **Spring Boot**: Simplified configuration and development with Java.
- **RESTful APIs**: CRUD operations and business logic exposed via HTTP methods.
- **Exception Handling**: Custom exception handling to ensure robust and user-friendly error responses.
- **Database Integration**: Each service connects to its respective database, ensuring data persistence.

## Technologies

- **Java**: The main programming language used for backend development.
- **Spring Boot**: Framework for building Java applications.
- **Eureka**: Service discovery tool for managing service instances.
- **MySQL**: Relational database used for data storage.
- **REST**: API design approach for interacting with services.
- **Maven/Gradle**: Dependency management and build automation tools.

## Microservices Overview

1. **AccountService**: Manages user accounts and their details, including balance and IBAN.
2. **BillingService**: Handles billing information, automatic payments, and billing history.
3. **InvestmentService**: Manages user investments, tracks portfolio performance, and interacts with external financial data sources.
4. **TransactionService**: Manages financial transactions, ensuring secure and accurate money transfers between accounts.

## Setup and Installation

### Prerequisites

- **Java 11** or higher
- **Maven** or **Gradle**
- **MySQL** (or another preferred database)
- **Eureka Server** for service registration and discovery

### Clone the Repository

```bash
git clone https://github.com/yourusername/finance-application.git
cd finance-application
```

### Build and Run Services

Each microservice is a separate module within the project. To build and run them, navigate to each service directory and use Maven/Gradle commands:

```bash
cd accountservice
mvn clean install
mvn spring-boot:run
```

Repeat the above steps for each service: `billingservice`, `investmentservice`, `transactionservice`.

### Eureka Server

Ensure the Eureka Server is up and running before starting the microservices:

```bash
cd eureka-server
mvn clean install
mvn spring-boot:run
```

## Usage

Once all services are running, you can interact with them via RESTful API calls using tools like `Postman` or `cURL`.

### Example:

- **Create an Account**:
  ```bash
  POST /accounts
  {
    "username": "john_doe",
    "iban": "TR123456789012345678901234",
    "balance": 1000.00
  }
  ```

- **Transfer Money**:
  ```bash
  POST /accounts/transfer
  {
    "senderIban": "TR123456789012345678901234",
    "receiverIban": "TR987654321098765432109876",
    "amount": 100.00
  }
  ```

## API Endpoints

### AccountService
- `GET /accounts`: Retrieve all accounts.
- `GET /accounts/{id}`: Retrieve an account by ID.
- `POST /accounts`: Create a new account.
- `PUT /accounts/user/{username}`: Update account details.
- `DELETE /accounts/{id}`: Delete an account.
- `POST /accounts/transfer`: Transfer money between accounts.

### BillingService
- `GET /billing`: Retrieve all bills.
- `POST /billing`: Create a new bill.
- `POST /billing/pay/{id}`: Pay a bill.

### InvestmentService
- `GET /investments`: Retrieve all investments.
- `POST /investments/buy`: Buy an investment.
- `POST /investments/sell`: Sell an investment.
