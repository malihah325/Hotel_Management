🏨 Hotel Management System (Backend)
📌 Overview

The Hotel Management System Backend is built using Spring Boot to handle core hotel operations such as managing rooms, customers, and bookings.
This backend provides REST APIs for creating, updating, and retrieving hotel-related data, including booking workflows, payment methods, and room availability.

It is designed following clean architecture principles with proper separation of concerns into Controllers, Services, Repositories, and DTO Converters.

⚙️ Tech Stack

Java 17+

Spring Boot 3+

Spring Data JPA (Hibernate)

MySQL / PostgreSQL

Lombok

Thymeleaf (for server-side rendering if required)

Maven / Gradle

📂 Project Structure
hotel-management-backend/
 ├── src/main/java/com/example/hotelmanagement/
 │   ├── controller/        # REST Controllers (Room, Customer, Booking)
 │   ├── service/           # Business logic services
 │   ├── repository/        # Spring Data JPA Repositories
 │   ├── dto/               # Data Transfer Objects
 │   ├── helperClass/       # Entity <-> DTO Converters
 │   ├── model/             # JPA Entities (Room, Customer, Booking)
 │   └── HotelManagementApplication.java
 ├── src/main/resources/
 │   ├── application.properties  # DB Configurations
 │   └── schema.sql / data.sql   # Optional DB Init
 └── pom.xml

🏗️ Features Implemented

✅ Room Management

Add, update, delete, and fetch rooms

Manage room types, availability, and pricing

✅ Customer Management

Register customers with role support (Admin, Guest, etc.)

Store contact details and authentication info

✅ Booking Management

Create and manage bookings

Check-in & Check-out workflow

Apply discounts and calculate final prices

Support multiple payment methods

✅ DTO Conversion Layer

Clean separation between entities and DTOs

Easy request/response mapping

🔑 Example API Usage
1️⃣ Create Booking (POST /api/bookings)

Request Body Example:

{
  "checkInDate": "2025-08-17",
  "checkOutDate": "2025-08-20",
  "roomId": 1,
  "customerId": 5,
  "paymentMethod": "ACCOUNT",
  "totalPrice": 500.0,
  "bookingStatus": "CONFIRMED",
  "discountApplied": true,
  "accountNumber": "123456789",
  "checkedIn": false,
  "discountAmount": 50.0
}


Response Example:

{
  "id": 1001,
  "checkInDate": "2025-08-17",
  "checkOutDate": "2025-08-20",
  "totalPrice": 500.0,
  "bookingStatus": "CONFIRMED",
  "room": {
    "id": 1,
    "roomType": "SINGLE",
    "pricePerDay": 200.0,
    "status": "BOOKED"
  },
  "customer": {
    "id": 5,
    "customerName": "John Doe",
    "email": "john@example.com",
    "role": "USER"
  }
}

🛠️ Setup & Installation
1️⃣ Clone the Repository
git clone https://github.com/your-username/hotel-management-backend.git
cd hotel-management-backend

2️⃣ Configure Database

Update src/main/resources/application.properties:

spring.datasource.url=jdbc:mysql://localhost:3306/hotel_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true




The backend will run at 👉 http://localhost:8080

🚀 Future Enhancements

JWT Authentication & Role-based Access

Payment Gateway Integration

Email/SMS Notifications for bookings

Reporting & Analytics

👨‍💻 Author

Developed by Mlaiha Habib
🔗 GitHub: malihah325