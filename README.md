# user-data-service

# Introduction 
The User Data Service is a crucial component of a real-time chat application. It is responsible for handling user CRUD (Create, Read, Update, Delete) operations and implementing caching mechanisms for frequently accessed data.

## Getting Started

### Prerequisites
- [Docker](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/linux/)

### Installation and Setup
1. Clone the repository.
2. Navigate to the project directory.
3. Run the following command to start the service:

`sudo docker-compose up`

## Usage

### Endpoints

The User Data Service exposes the following endpoints for interacting with user data:

#### GET ALL Users

- Endpoint: `localhost:8080/user`
- Method: GET
- Description: Retrieves information about all users.

#### GET User by ID

- Endpoint: `localhost:8080/user/{id}`
- Method: GET
- Description: Retrieves information about a specific user based on their ID.

#### POST User

- Endpoint: `localhost:8080/user`
- Method: POST
- Description: Creates a new user with the provided data.

#### PUT User

- Endpoint: `localhost:8080/user`
- Method: PUT
- Description: Updates an existing user with the provided data.

#### DELETE User by ID

- Endpoint: `localhost:8080/user/{id}`
- Method: DELETE
- Description: Deletes a user based on their ID.

Note: Access to the APIs requires authentication, except for the POST API, which is accessible without login.

#### User Login

- Endpoint: `localhost:8080/login`
- Method: POST
- Description: Allows users to log in by providing their username and password as form data.

## Contributing

Contributions are welcome! If you would like to contribute to the User Data Service, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make the necessary changes and commit them.
4. Push your changes to your forked repository.
5. Submit a pull request.



