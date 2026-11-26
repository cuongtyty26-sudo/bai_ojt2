# bai_ojt2

Dự án web Spring Boot 3 (Java 17) sử dụng Thymeleaf, Spring Security, Spring Data JPA, Validation, MySQL và Flyway.

## Công nghệ sử dụng
- **Java**: 17
- **Spring Boot**: 3.3.4
- **Web & View**: Spring Web MVC, Thymeleaf, Thymeleaf Extras Spring Security 6
- **Security**: Spring Security 6
- **Database & Migration**: MySQL, Spring Data JPA, Flyway
- **Validation**: Jakarta Bean Validation (`spring-boot-starter-validation`)

## Cấu trúc thư mục theo tầng (`com.example.baiojt2`)
```text
src/main/java/com/example/baiojt2/
├── config/       # Cấu hình hệ thống (SecurityConfig, ...)
├── controller/   # Web/MVC controllers (HomeController, ...)
├── dto/          # Data Transfer Objects
├── entity/       # JPA Entities
├── repository/   # Spring Data JPA Repositories
└── service/      # Business logic services
```

## Cấu hình môi trường (`dev`)

Profile mặc định là `dev` (`SPRING_PROFILES_ACTIVE=dev`). Cấu hình đọc các biến môi trường DB sau (xem [.env.example](.env.example)):

| Biến môi trường | Mặc định | Mô tả |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `dev` | Profile chạy ứng dụng |
| `SERVER_PORT` | `8080` | Cổng HTTP server |
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `bai_ojt2` | Tên database (tự tạo nếu chưa có) |
| `DB_USERNAME` | `root` | Tên đăng nhập MySQL |
| `DB_PASSWORD` | *(trống)* | Mật khẩu MySQL |

## Hướng dẫn chạy dự án sau khi clone

```bash
git clone https://github.com/cuongtyty26-sudo/bai_ojt2.git
cd bai_ojt2

# Xuất biến môi trường DB (thay mật khẩu MySQL của bạn nếu có)
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=bai_ojt2
export DB_USERNAME=root
export DB_PASSWORD=your_mysql_password

# Chạy test
./mvnw test

# Chạy ứng dụng
./mvnw spring-boot:run
```

Truy cập `http://localhost:8080/` để xem trang chủ trống.

## Quy ước làm việc nhóm
Xem chi tiết tại [CONTRIBUTING.md](CONTRIBUTING.md).

## Flyway database migrations and demo data (T4E7A5D)

The application uses Flyway to create the schema and load synthetic development data automatically at startup.

### What runs

- `V1__init.sql`: creates all 13 tables from the approved ERD, including foreign keys, unique constraints, indexes and validation checks.
- `V2__seed_demo_data.sql`: inserts 1 admin, 3 recruiter/partner accounts with 3 companies, 15 fictional student candidate profiles (with education, skills, projects and certificates), and 30 skills. It also inserts small demo examples for shortlists, interview requests, review history, notifications and profile views.
- All demo identities use reserved `.test` / `.local` domains and are fictional. Do not use this seed data in production.

### Start with an empty MySQL database

1. Install and start MySQL 8.0.16+ (để các CHECK constraints trong V1 được thực thi).
2. Copy `.env.example` to `.env` if you use a dotenv loader, or export the variables in your shell. Set `DB_PASSWORD` to your local MySQL password. The application itself reads environment variables.
3. The default JDBC URL includes `createDatabaseIfNotExist=true`, so MySQL creates the configured database if the DB user has permission.
4. Run:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Flyway runs before Hibernate validation. On a new database, it executes V1 then V2 and creates the tables and demo rows. Hibernate uses `ddl-auto=validate`; it does not create or modify tables. Flyway records applied versions in `flyway_schema_history`, so restarting the application does not duplicate seed rows.

To verify from MySQL:

```sql
USE bai_ojt2;
SHOW TABLES;
SELECT COUNT(*) AS user_count FROM users;
SELECT COUNT(*) AS company_count FROM companies;
SELECT COUNT(*) AS profile_count FROM profiles;
SELECT COUNT(*) AS skill_count FROM skills;
```

Expected counts after the first successful migration: 19 users (1 admin + 3 recruiters + 15 candidates), 3 companies, 15 profiles and 30 skills.

### Local demo accounts

**Password for all accounts:** `Demo@12345`

| Role | Email |
|---|---|
| ADMIN | `admin@demo.local` |
| RECRUITER / Partner 1 | `recruiter1@demo.local` |
| RECRUITER / Partner 2 | `recruiter2@demo.local` |
| RECRUITER / Partner 3 | `recruiter3@demo.local` |
| CANDIDATE | `candidate01@demo.local` through `candidate15@demo.local` (15 separate fictional accounts) |

The password is stored as a BCrypt hash in the seed migration. Change or remove all demo credentials before any deployment beyond local development.

### Resetting a local database

Flyway migrations are versioned and should not be edited after they have run in a shared environment. To rerun from scratch in a disposable local database, drop and recreate the local database, then start the application again. **This deletes all local data. Do not run against a database containing data you need.**
