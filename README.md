🏨 Hotel Management System

A full-stack hotel management application  built with Spring Boot, Thymeleaf**, and MySQL.
It provides functionalities for admins and customers, including room booking, payments, and user authentication.

🚀 Features

🔹 Customer

* Sign up and log in securely (Spring Security).
* Browse available rooms based on check-in/check-out dates.
* Make bookings and view booking history.
* Pay for bookings with validation (insufficient/excess payments handled).
* Cancel bookings (if not already paid).

🔹 Admin

* Manage rooms (CRUD operations).
* View and manage customer details.
* Track bookings with status.
* Manage payments linked to bookings.

---

🛠️ Tech Stack

* Backend: Spring Boot (Java), Spring Data JPA, Spring Security
* Frontend: Thymeleaf, HTML, CSS, JavaScript (with modal-based login/signup)
* Database: MySQL (JPA/Hibernate ORM)
* Libraries: Lombok, Validation API
* Build Tool: Gradel.

---

⚙️ Installation

### 1️⃣ Clone the repository

```bash
git clone https://github.com/your-username/hotel-management-system.git
cd hotel-management-system
```

 2️⃣ Configure Database (MySQL)

Create a database in MySQL:

```sql
CREATE DATABASE hotel;
```

Update your `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hotel
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

 3️⃣ Build and Run
 ``Run



### 4️⃣ Access the Application

* 🌐 Open in browser: `http://localhost:8080`
* 👤 Default Admin Login:

  * **Username:** `admin@123`
  * **Password:** `admin123`

---

## 📌 Project Structure

```
src/main/java/com/example/hotelmanagement/
│── controller/        # Web Controllers (Admin, Customer, home, Signup)
│── dto/               # Data Transfer Objects
│── entity/            # Entities (Booking, Customer, Payment, Room)
│── enums/             # Enums (BookingStatus, PaymentMethod, Role, RoomStatus)
│── handler/           # Exception Handlers
│── repositories/      # Spring Data JPA Repositories
│── services/          # Business Logic (BookingService, PaymentService, etc.)
│── converters/        # DTO ↔ Entity converters
```

---

📸 Screenshots

 🏠 Customer Dashboard

(https://docs.google.com/document/d/1Z99MqGrCvgY44sBK2PygYeyYrG7fYgQ0Z8MI27_qZW4/edit?tab=t.0)

📋 Admin Room Management
(https://docs.google.com/document/d/113UENFGLlfBGKMGB91NNzde_DAlC006e_BUCnFIFcWI/edit?tab=t.0)

---

 🔒 Security

* Spring Security with role-based access (`ADMIN` & `CUSTOMER`).
* Passwords stored securely using hashing.
* Customers cannot access admin pages.

---

 🐛 Known Issues

* ✅ Prevents double booking with overlapping dates.
* ✅ Validates payment against total amount.
* 🔜 Controller endpoints for external integrations.

---

✨ **Built with passion using Spring Boot & Thymeleaf** ✨

---
Author:Maliha Habib.
