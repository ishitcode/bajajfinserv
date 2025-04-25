# Bajaj Finserv Health Challenge

A Spring Boot application that automatically interacts with a remote API at application startup to solve the Nth-Level Followers problem.

## Problem Statement

Build a Spring Boot application that:
1. Calls the `/generateWebhook` endpoint on startup
2. Solves the assigned problem (Nth-Level Followers)
3. Sends the result to the provided webhook with JWT authentication

## Technical Requirements

- Spring Boot 3.2.3
- Java 17
- Maven 3.9.0 or higher
- WebClient for HTTP requests
- JWT authentication
- Automatic startup execution (no manual trigger)

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── bajaj/
│   │           ├── HealthChallengeApplication.java
│   │           ├── config/
│   │           │   └── WebhookConfig.java
│   │           ├── model/
│   │           │   ├── WebhookRequest.java
│   │           │   ├── WebhookResponse.java
│   │           │   └── ResultResponse.java
│   │           └── service/
│   │               └── WebhookService.java
│   └── resources/
│       └── application.properties
```

## API Endpoints

### 1. Generate Webhook
- **URL**: `https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook`
- **Method**: POST
- **Request Body**:
  ```json
  {
      "name": "John Doe",
      "regNo": "REG12347",
      "email": "john@example.com"
  }
  ```
- **Response**:
  ```json
  {
      "webhook": "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook",
      "accessToken": "jwt_token_here",
      "data": {
          "users": {
              "users": [...],
              "findId": 1,
              "n": 2
          }
      }
  }
  ```

### 2. Test Webhook
- **URL**: Received from generateWebhook response
- **Method**: POST
- **Headers**:
  - `Authorization: Bearer <token>`
  - `Content-Type: application/json`
- **Request Body**:
  ```json
  {
      "regNo": "REG12347",
      "outcome": [4, 5]
  }
  ```

## Problem Solution (Nth-Level Followers)

Given:
- A start ID (findId)
- nth level
- List of users and their follows

Find:
- User IDs that are exactly n levels away in the "follows" list

Example:
```json
{
    "users": {
        "users": [
            {"id": 1, "name": "Alice", "follows": [2, 3]},
            {"id": 2, "name": "Bob", "follows": [4]},
            {"id": 3, "name": "Charlie", "follows": [4, 5]},
            {"id": 4, "name": "David", "follows": [6]},
            {"id": 5, "name": "Eva", "follows": [6]},
            {"id": 6, "name": "Frank", "follows": []}
        ],
        "findId": 1,
        "n": 2
    }
}
```

Solution:
- Start from findId: 1 (Alice)
- Level 1: Alice follows [2, 3]
- Level 2: 
  - Through ID 2 (Bob): follows [4]
  - Through ID 3 (Charlie): follows [4, 5]
- Users exactly 2 levels away: [4, 5]

## Setup and Running

1. **Prerequisites**:
   - Java 17
   - Maven 3.9.0 or higher

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Verify the application**:
   - The application will automatically:
     - Call the generateWebhook endpoint
     - Process the response
     - Send the result to the webhook
     - Retry up to 4 times if needed

## Testing with Postman

1. **Create a new collection**:
   - Name: "Bajaj Health Challenge"

2. **First Request (Generate Webhook)**:
   - Method: POST
   - URL: `https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook`
   - Headers: `Content-Type: application/json`
   - Body: See Request Body section above

3. **Second Request (Test Webhook)**:
   - Method: POST
   - URL: From generateWebhook response
   - Headers: 
     - `Authorization: Bearer <token>`
     - `Content-Type: application/json`
   - Body: See Request Body section above

## Troubleshooting

1. **401 Unauthorized Error**:
   - Verify the token is fresh (tokens expire quickly)
   - Check the Authorization header format
   - Ensure the token is properly set in the environment

2. **400 Bad Request Error**:
   - Verify the request body format
   - Check the Content-Type header
   - Ensure the webhook URL is correct

3. **Application Startup Issues**:
   - Check Java version (must be 17)
   - Verify Maven installation
   - Check network connectivity

## License

This project is part of the Bajaj Finserv Health Challenge.  
