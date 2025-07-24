# E-Banking Transaction Portal
This is Java Spring Boot application to retreive and manage E-Banking transactions being made by the customer.
It contains pagination transactions and conversion capabilities.
<img width="2640" height="1237" alt="image" src="https://github.com/user-attachments/assets/365cd746-99b1-43fe-ac92-dd71844c5f3d" />

This architecture allows user to retrieve the data via REST API and if there is a third party incoming request regardig the user's transaction based on the transactionId, it will save on the database.
The controller will enable to have scalability of endpoints needed in the future. Each services tied to some controller and able to call many repositories because it's a location where the business logic happens.

### Core Functionality
- JWT Authentication
- Paginated Transactions
- Transaction Summaries

### Technical Features
- Security: using JWT implementation
- Containerized: using Docker
- API Documentation: using Swagger



