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
