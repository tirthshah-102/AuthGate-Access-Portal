# 🛡️ AuthGate - Enterprise Residential Access Control Portal

AuthGate is an enterprise-grade, self-contained visitor access management system designed for residential societies. The application replaces insecure paper logs with dynamic, time-bound **QR Code Gate Passes**.

Built on the **Java Spring Boot** framework, AuthGate integrates **Spring Security** for role-based authentication, **Thymeleaf** for server-side view templates, an embedded **H2 Database** for persistent storage, and **ZXing** for server-side QR generation. The frontend features a custom **glassmorphic design system** with a built-in webcam scanner.

---

## 📂 Project Directory Structure

Below is the directory map of the AuthGate codebase to help you navigate the system:

```text
authgate-project/
│
├── .mvn/                                # Maven Wrapper configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/society/gatepass/
│   │   │       ├── controller/
│   │   │       │   ├── AdminController.java     # Resident CRUD, stats, and audit logs
│   │   │       │   ├── AuthController.java      # Login mapping and role routing
│   │   │       │   ├── GuardController.java     # Scan page, verification, check-in/out
│   │   │       │   └── ResidentController.java  # Pass form, pass ledger, QR display
│   │   │       │
│   │   │       ├── model/
│   │   │       │   ├── User.java                # Resident, Guard, Admin entity
│   │   │       │   ├── GatePass.java            # Pass details, validity, and UUID token
│   │   │       │   └── PassLog.java             # Entry/exit logs with guard remarks
│   │   │       │
│   │   │       ├── repository/
│   │   │       │   ├── UserRepository.java      # Database operations for User
│   │   │       │   ├── GatePassRepository.java  # Database operations for GatePass
│   │   │       │   └── PassLogRepository.java   # Database operations for PassLog
│   │   │       │
│   │   │       ├── security/
│   │   │       │   └── SecurityConfig.java      # BCrypt, path filters, success handlers
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── UserService.java         # User authentication & registration
│   │   │       │   ├── GatePassService.java     # Check-in, check-out state machine
│   │   │       │   └── QRCodeService.java       # ZXing Base64 image generator
│   │   │       │
│   │   │       ├── DataInitializer.java         # Seeder for demo accounts & test passes
│   │   │       └── GatepassApplication.java     # Spring Boot application entry point
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       │   └── css/
│   │       │       └── styles.css               # Global glassmorphic stylesheet
│   │       │
│   │       ├── templates/
│   │       │   ├── admin/
│   │       │   │   ├── dashboard.html           # Admin stats & recent activity feed
│   │       │   │   ├── residents.html           # Resident account manager
│   │       │   │   └── logs.html                # Complete society entry/exit log
│   │       │   │
│   │       │   ├── guard/
│   │       │   │   ├── dashboard.html           # Live camera scanner & uploader
│   │       │   │   └── verify.html              # Verification alerts & check-in/out forms
│   │       │   │
│   │       │   ├── resident/
│   │       │   │   ├── dashboard.html           # Personal ledger of passes
│   │       │   │   ├── create-pass.html         # Form to generate a new gate pass
│   │       │   │   └── view-pass.html           # Scannable QR card & print view
│   │       │   │
│   │       │   └── login.html                   # Central login portal
│   │       │
│   │       └── application.properties           # Database, JPA, and quiet logging settings
│   │
│   └── test/
│       └── java/com/society/gatepass/
│           └── GatepassApplicationTests.java    # Application context loading tests
│
├── .gitattributes                           # Git attribute config
├── .gitignore                               # Git exclusions (target, H2 binaries, IDE files)
├── mvnw                                     # Maven Wrapper bash script (Linux/macOS)
├── mvnw.cmd                                 # Maven Wrapper batch script (Windows)
├── pom.xml                                  # Project dependencies (Java 21, Boot 4.1.0)
├── README.md                                # Comprehensive documentation (this file)
└── run.bat                                  # Quiet-mode Windows execution script
```

---

## 🛠️ Technology Stack Specifications

| Layer | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Runtime Environment** | Java OpenJDK | `21` | LTS runtime environment. |
| **Backend Core** | Spring Boot | `4.1.0` | Application skeleton & dependency container. |
| **Security Layer** | Spring Security | `6.x` / Boot Integrated | Role-based path filtering & session-based authentication. |
| **ORM / Data Access** | Spring Data JPA | `4.1.0` / Hibernate 7.4.x | Relational mapping, SQL translations, transaction controls. |
| **Template Engine** | Thymeleaf | `4.1.0` | Server-rendered HTML templates with security tags. |
| **Database** | H2 Database Engine | `2.4.240` | Embedded file-persistent SQL database. |
| **QR Code Engine** | ZXing (Zebra Crossing) | `3.5.3` | Server-side QR barcode generation. |
| **Client-Side Scanner** | HTML5 QR Code | `2.3.8` | In-browser live camera stream and image uploader. |
| **Styling** | Vanilla CSS | *Modern CSS3* | High-performance custom glassmorphism design (zero framework lag). |

---

## 🔐 Security & Access Control Matrix

The application enforces path-based role checking. If a user attempts to access an unauthorized path, Spring Security intercepts the request and throws a `403 Forbidden` error.

| Request Pattern | Allowed Roles | Authentication Required? | Redirect / Action |
| :--- | :--- | :--- | :--- |
| `/login` | Any | No | Renders login page |
| `/css/**`, `/js/**` | Any | No | static resources bypass security |
| `/h2-console/**` | Any (Dev mode) | No | Database browser (CSRF bypassed) |
| `/` | Authenticated | Yes | Redirects dynamically based on user role |
| `/admin/**` | `ROLE_ADMIN` | Yes | Admin views (Dashboard, Residents, Logs) |
| `/resident/**` | `ROLE_RESIDENT` | Yes | Resident views (Dashboard, Pass Form, QR Card) |
| `/guard/**` | `ROLE_GUARD` | Yes | Guard views (Scanner, Verify details, Check events) |

---

## 🔗 Endpoint API Documentation

### 1. Central Authentication Mappings

- **`GET /login`**
  - **Access**: Public
  - **Output**: `login.html` template.
  - **Action**: Handles rendering of login fields and displays query alerts (`?error` or `?logout`).
- **`GET /`**
  - **Access**: Authenticated (Any role)
  - **Action**: Inspects session authorities and redirects:
    - `ROLE_ADMIN` &rarr; `/admin/dashboard`
    - `ROLE_GUARD` &rarr; `/guard/dashboard`
    - `ROLE_RESIDENT` &rarr; `/resident/dashboard`

### 2. Resident Mappings (`/resident`)

- **`GET /resident/dashboard`**
  - **Access**: `ROLE_RESIDENT`
  - **Output**: `resident/dashboard.html`
  - **Action**: Fetches and lists all visitor passes created by the logged-in resident (sorted by latest).
- **`GET /resident/create-pass`**
  - **Access**: `ROLE_RESIDENT`
  - **Output**: `resident/create-pass.html`
  - **Action**: Instantiates a blank `GatePass` object bound with default start and end times (4-hour window) and renders the generation form.
- **`POST /resident/create-pass`**
  - **Access**: `ROLE_RESIDENT`
  - **Input**: `@ModelAttribute("gatePass")` form data.
  - **Action**: Validates form fields (Guest name/phone/vehicle). Generates a random UUID `passToken`, flags status as `ACTIVE`, links the pass to the active resident, and saves it.
  - **Redirect**: `/resident/view-pass/{token}`
- **`GET /resident/view-pass/{token}`**
  - **Access**: `ROLE_RESIDENT`
  - **Output**: `resident/view-pass.html`
  - **Action**: Retrieves the gate pass using the token. Calls the `QRCodeService` to generate a Base64-encoded QR code PNG. Supplies the pass details and Base64 string to the Thymeleaf model.

### 3. Guard Mappings (`/guard`)

- **`GET /guard/dashboard`**
  - **Access**: `ROLE_GUARD`
  - **Output**: `guard/dashboard.html`
  - **Action**: Renders the manual search, file upload scanner, and live webcam scanner. Displays a list of all check-ins/check-outs authorized by this guard today.
- **`GET /guard/verify`**
  - **Access**: `ROLE_GUARD`
  - **Output**: `guard/verify.html`
  - **Parameters**: `token` (String)
  - **Action**: Searches the database for the pass matching the token. Calculates if the pass is active, expired, or already used. Returns validation flags to render the warning/success banners.
- **`POST /guard/check-in`**
  - **Access**: `ROLE_GUARD`
  - **Parameters**: `token` (String), `notes` (String, optional)
  - **Action**: Transitions the pass status to `CHECKED_IN`. Creates a new `PassLog` record with the entry time, pass link, and guard metadata.
  - **Redirect**: `/guard/dashboard?success=checkedin`
- **`POST /guard/check-out`**
  - **Access**: `ROLE_GUARD`
  - **Parameters**: `token` (String), `notes` (String, optional)
  - **Action**: Transitions the pass status to `CHECKED_OUT`. Locates the active `PassLog` (where `exitTime` is null) and records the current timestamp as the exit time.
  - **Redirect**: `/guard/dashboard?success=checkedout`

### 4. Admin Mappings (`/admin`)

- **`GET /admin/dashboard`**
  - **Access**: `ROLE_ADMIN`
  - **Output**: `admin/dashboard.html`
  - **Action**: Renders overall indicators (Total residents, active passes, visitors inside) and lists the recent gate logs.
- **`GET /admin/residents`**
  - **Access**: `ROLE_ADMIN`
  - **Output**: `admin/residents.html`
  - **Action**: Lists all registered residents and attaches a blank `User` model to bind the new resident registration form.
- **`POST /admin/residents`**
  - **Access**: `ROLE_ADMIN`
  - **Input**: `@ModelAttribute("newResident")` resident user details.
  - **Action**: Checks if the username is unique. Encrypts the password using BCrypt, assigns `ROLE_RESIDENT`, and saves the user.
  - **Redirect**: `/admin/residents?success=added`
- **`GET /admin/residents/delete/{id}`**
  - **Access**: `ROLE_ADMIN`
  - **Action**: Deletes the resident record matching the ID.
  - **Redirect**: `/admin/residents?success=deleted`
- **`GET /admin/logs`**
  - **Access**: `ROLE_ADMIN`
  - **Output**: `admin/logs.html`
  - **Action**: Fetches the complete chronological entry and exit history for the entire society.

---

## 🗄️ Database Tables Schema Specs

Hibernate auto-generates the database schema using the entity definitions. The detailed tables are described below:

### 1. `users` Table
Stores login accounts, credentials, and roles.
- `id`: `BIGINT` (Primary Key, Auto-Increment)
- `username`: `VARCHAR(50)` (Unique, Not Null)
- `password`: `VARCHAR(100)` (Not Null, BCrypt Encoded)
- `fullName`: `VARCHAR(255)` (Not Null)
- `flatNumber`: `VARCHAR(255)` (Nullable, e.g., A-101 for residents)
- `phoneNumber`: `VARCHAR(255)` (Not Null)
- `role`: `VARCHAR(255)` (Not Null, e.g., `ROLE_ADMIN`, `ROLE_RESIDENT`, `ROLE_GUARD`)

### 2. `gate_passes` Table
Stores individual visitor invite passes generated by residents.
- `id`: `BIGINT` (Primary Key, Auto-Increment)
- `passToken`: `VARCHAR(255)` (Unique, Not Null, UUID string)
- `visitorName`: `VARCHAR(255)` (Not Null)
- `visitorPhone`: `VARCHAR(255)` (Not Null)
- `visitorVehicleNo`: `VARCHAR(255)` (Nullable)
- `purpose`: `VARCHAR(255)` (Not Null)
- `validFrom`: `TIMESTAMP` (Not Null)
- `validTo`: `TIMESTAMP` (Not Null)
- `status`: `VARCHAR(255)` (Not Null, e.g., `ACTIVE`, `CHECKED_IN`, `CHECKED_OUT`)
- `resident_id`: `BIGINT` (Foreign Key referencing `users(id)`, Not Null)
- `createdAt`: `TIMESTAMP` (Not Null)

### 3. `pass_logs` Table
Audits actual entry and exit times of visitors at the gate.
- `id`: `BIGINT` (Primary Key, Auto-Increment)
- `gate_pass_id`: `BIGINT` (Foreign Key referencing `gate_passes(id)`, Not Null)
- `guard_id`: `BIGINT` (Foreign Key referencing `users(id)`, Nullable)
- `entryTime`: `TIMESTAMP` (Nullable, logged on check-in)
- `exitTime`: `TIMESTAMP` (Nullable, logged on check-out)
- `notes`: `VARCHAR(255)` (Nullable)

---

## ⚙️ Core Technical Mechanism Details

### Server-Side Base64 QR Generation
When a resident views a pass, the controller calls `QRCodeService.java`. The service:
1. Receives the `passToken` (UUID) string.
2. Uses the **ZXing QRCodeWriter** to encode the token into a 2D `BitMatrix` structure.
3. Converts the matrix into a PNG stream using `MatrixToImageWriter`.
4. Encodes the stream's byte array into a Base64 String using Java's `Base64.getEncoder()`.
5. The Thymeleaf view renders this string in-memory without saving any image file on the server's hard drive:
   ```html
   <img th:src="'data:image/png;base64,' + ${qrCode}" alt="Pass QR Code">
   ```

### In-Browser Camera & File Scanning
In `guard/dashboard.html`, we initialize the **HTML5 QR Code JS scanner**:
1. **Webcam Scan**: Binds to a container `<div id="reader"></div>`. It starts a webcam stream capture. The library decodes the QR patterns frame-by-frame. Once a match is found, it calls the `onScanSuccess` callback and redirects to `/guard/verify?token=<token>`.
2. **File Scan**: An `<input type="file">` change listener reads the selected image file. The library decodes it in-browser, immediately extracting the token string and redirecting without hitting any API.

---

## 🛠️ Step-by-Step Installation & Run Guide

### Option A: Command Line Startup (Simplified & Clean)
1. Open your terminal (PowerShell or cmd) at the project root `D:\QR code based gate pass system for residential society`.
2. Clean and compile the codebase:
   ```cmd
   .\mvnw clean compile
   ```
3. Run the optimized startup script:
   ```cmd
   .\run.bat
   ```
   *(This launches the application in Maven quiet mode `-q`, hiding lengthy dependency check logs and printing only local URLs and credentials).*
4. Open [http://localhost:8080](http://localhost:8080) in your web browser.

### Option B: Running in IntelliJ IDEA
1. Open IntelliJ IDEA.
2. Go to **File -> Open** and choose the directory: `D:\QR code based gate pass system for residential society`.
3. IntelliJ will detect `pom.xml` and sync all dependencies.
4. Ensure the project JDK is set to **Java 21** (**File -> Project Structure -> Project**).
5. Open `src/main/java/com/society/gatepass/GatepassApplication.java`.
6. Right-click the file and click **Run 'GatepassApplication'**.
7. Access the port at [http://localhost:8080](http://localhost:8080).

---

## 👥 Seeded Demo Login Accounts

Test the application instantly using these seeded accounts:

- **System Admin**: Username `admin` / Password `admin123`
- **Host Resident (Flat A-101)**: Username `resident1` / Password `pass123`
- **Host Resident (Flat B-202)**: Username `resident2` / Password `pass123`
- **Security Guard**: Username `guard` / Password `guard123`

*Quick Scan Verification Tip:*
- Copy the token **`active-pass-token`** (valid pass) or **`expired-pass-token`** (expired pass) and paste it into the Guard's verify token field to test.


