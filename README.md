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

## Shared Thymeleaf layout (T4E7A5E)

All application pages should reuse the common layout at `templates/fragments/layout.html`. It composes the role-aware header, global flash/feedback messages, page content and footer. Bootstrap 5.3.3 is loaded from the official jsDelivr CDN, with project-wide styling in `static/css/app.css`.

### Fragments

- `fragments/header.html`: OJT Connect logo, home link, role-specific navigation, authenticated username, role badge, login link for anonymous visitors and POST logout form.
- `fragments/footer.html`: shared footer.
- `fragments/alerts.html`: success/error flash messages plus login-error and logout feedback.
- `fragments/layout.html`: common HTML document, Bootstrap assets and content slot.

The `thymeleaf-extras-springsecurity6` dependency was already present in `pom.xml`; no extra dependency is required. The header uses `sec:authorize` and `sec:authentication` to display navigation for `ROLE_ADMIN`, `ROLE_RECRUITER` and `ROLE_CANDIDATE`.

### Reuse the layout on new pages

Create a content fragment and replace the page root with the shared layout:

```html
<!DOCTYPE html>
<html lang="vi"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
      th:replace="~{fragments/layout :: layout('Tên trang', ~{::content})}">
<body>
<section th:fragment="content">
    <h1>Nội dung trang</h1>
</section>
</body>
</html>
```

### Role/authority convention

The database role values are `ADMIN`, `RECRUITER`, and `CANDIDATE`. The Spring Security `GrantedAuthority` values supplied by the authentication layer must be `ROLE_ADMIN`, `ROLE_RECRUITER`, and `ROLE_CANDIDATE` because `hasRole('ADMIN')` checks the `ROLE_` prefix. Authentication/UserDetails wiring is handled separately from this layout task.

The menu URLs (`/admin/users`, `/admin/reviews`, `/recruiter/candidates`, `/recruiter/shortlists`, `/recruiter/interviews`, `/candidate/profile`, and `/candidate/interviews`) are navigation targets for the corresponding feature tasks; they are not implemented by this layout-only change.


## Error handling and flash messages (T4E7A5F)

### Friendly error pages

- `GlobalExceptionHandler` (`@ControllerAdvice`) handles application exceptions centrally.
- Throw `BusinessException` for a business-rule violation. Its message is treated as safe user-facing text and is shown once after redirecting to the home page.
- `EntityNotFoundException` and `NoSuchElementException` return the friendly `error/404.html` page.
- Unexpected exceptions return `error/500.html`; the exception is written to server logs only. Stack traces and exception messages are not rendered in the browser.
- Spring Security forwards access-denied requests to `/error/403`, which displays `error/403.html`. The page does not expose protected resource details.
- Spring Boot's default error endpoint resolves `error/404.html` and `error/500.html` for unmatched routes and server errors. Error response details are disabled in `application.properties`.

### Flash messages after a successful or failed operation

Use `RedirectAttributes` in MVC POST/PUT/DELETE handlers. Flash attributes survive the redirect for one request and are displayed by the existing global `fragments/alerts.html` inside the shared layout.

Example success response:

```java
@PostMapping("/example")
public String create(@Valid @ModelAttribute ExampleForm form,
                     RedirectAttributes redirectAttributes) {
    exampleService.create(form);
    redirectAttributes.addFlashAttribute("successMessage", "Tạo dữ liệu thành công.");
    return "redirect:/example";
}
```

Example business-rule failure:

```java
if (alreadyExists) {
    throw new BusinessException("Dữ liệu này đã tồn tại.");
}
```

The shared alert fragment already displays `successMessage` and `errorMessage`; no page-specific alert markup is needed. Do not put SQL, stack traces, passwords, tokens or internal exception details in a `BusinessException` message.


## Authentication and authorization (T4E7A60)

### Login
- The custom login page is `/login` and authenticates by email + BCrypt password.
- Authentication uses the HTTP session; session fixation protection migrates the session after login.
- The optional `remember-me` checkbox creates a signed remember-me cookie valid for 14 days.
- Logout is a POST request to `/logout`, invalidates the session and deletes the session/remember-me cookies.
- CSRF remains enabled (Spring Security default, explicitly configured). Thymeleaf forms that submit POST requests must use `th:action` so the CSRF token is included.

### Roles and landing pages
- `ADMIN`: only `/admin/**`, landing page `/admin/`.
- `PARTNER`: only `/partner/**`, landing page `/partner/`.
- `STUDENT`: only `/me/**`, landing page `/me/`.
- Flyway migration `V3__rename_account_roles.sql` changes existing `RECRUITER` accounts to `PARTNER` and `CANDIDATE` accounts to `STUDENT` without deleting users.
- Accounts with `is_locked = true` cannot authenticate. Non-ACTIVE account statuses are also denied and the login page explains whether the account is pending, rejected, suspended or locked.

### Demo accounts
All accounts seeded by V2 use password `Demo@12345`:
- Admin: `admin@demo.local`
- Partner: `recruiter1@demo.local`, `recruiter2@demo.local`, `recruiter3@demo.local`
- Student: `candidate01@demo.local` through `candidate15@demo.local`

Set `SECURITY_REMEMBER_ME_KEY` to a long, random secret in deployed environments. The fallback value in application properties is for local development only.


## Student registration (T4E7A61)

- Registration page: `GET /register`; form submission: `POST /register`.
- Fields: full name, email, password and password confirmation.
- Bean Validation checks required values, email format and length limits.
- Custom class-level `@ValidRegistration` checks that the confirmation matches and that the email is not already registered; violations are attached to the relevant field.
- The form keeps the submitted full name and email when validation fails. Password fields are deliberately cleared and never echoed back into the page.
- Passwords are encoded with the shared BCrypt `PasswordEncoder`; plain-text passwords are never persisted.
- Successful registration creates a `STUDENT` account and a linked row in `profiles` in one transaction. The profile uses the supplied full name and `is_public = false` (private).
- Successful registration redirects to `/login?registered` and shows a one-time success message.
- CSRF remains enabled for the registration POST form through Thymeleaf `th:action`.


## Password management and OTP recovery (T4E7A62)

- Authenticated users can change their password at `GET/POST /account/password`; the current password must match the BCrypt hash before the new password is saved.
- Anyone can start account recovery at `/forgot-password`, submit their email, and enter the 6-digit OTP plus a new password at `/reset-password`.
- OTPs are generated with a cryptographically secure random generator, are valid for 10 minutes, and are stored only as BCrypt hashes in `password_reset_otps` (Flyway V4). The plain code is never written to the database.
- An OTP is single-use. The fifth incorrect attempt invalidates it; an expired OTP and an OTP replaced by a newer request are also invalidated.
- As requested for this OJT task, the generated code is written to the application log with the `DEMO PASSWORD RESET OTP` prefix instead of being emailed. This is strictly a development/testing substitute: disable/remove OTP logging and integrate a real email/SMS provider before production.
- After requesting a reset, watch the Spring Boot console for the OTP line, then enter the code on `/reset-password`.
- Unit tests cover current-password verification, OTP expiry, one-time consumption, and invalidation after five incorrect attempts.

Run the test suite with:

```bash
./mvnw clean test
```


## Student self-service profile (T4E7A64)

- Student profile page: `GET /me/profile`; update with `POST /me/profile`.
- The page displays and edits full name, date of birth, phone number, personal introduction, desired position and desired workplace.
- Flyway migration `V5__extend_student_profile.sql` adds the date-of-birth, desired-position and desired-workplace columns. Existing V1–V4 migrations are left unchanged.
- Bean Validation checks required/maximum full-name length, past date of birth, phone-number format and text length limits. Field-level messages are rendered next to each input. Invalid HTML date values are handled as binding errors.
- The controller obtains the account from Spring Security's authenticated principal. The service loads the profile only by that authenticated account's user ID; no user ID is accepted from the form or URL. A missing owned profile returns a not-found error.
- Only users with `ROLE_STUDENT` can access `/me/**`, as enforced by the existing SecurityConfig. CSRF protection remains enabled on the update form.

Run checks:

```bash
./mvnw clean test
```


## Admin skill catalog (T4E7A66)

- Admin skill catalog: `GET /admin/skills`; add at `GET/POST /admin/skills/new`; edit at `GET/POST /admin/skills/{skillId}/edit` and `POST /admin/skills/{skillId}`.
- Supported categories: LANGUAGE (Ngôn ngữ), FRAMEWORK, DATABASE (CSDL), TOOL (Công cụ), and SOFT_SKILL (Kỹ năng mềm).
- Skill names are trimmed and checked for duplicates without case sensitivity. The existing MySQL `skills.name` unique key uses `utf8mb4_0900_ai_ci`, which enforces case-insensitive uniqueness at database level as well. Duplicate names are shown as field-level form errors.
- Skills can be hidden or reactivated without deleting their student associations. A skill referenced by `profile_skills` cannot be deleted; the service checks usage before delete and the UI offers only hide for used skills. Unused skills may be permanently deleted after confirmation.
- Flyway migration `V7__extend_skills_catalog.sql` adds category and active/hidden state. V7 is intentionally used after the education migration V6 from task T4E7A65; existing migrations are not modified.
- The entire `/admin/**` area is restricted to `ROLE_ADMIN` by the existing SecurityConfig. All state-changing operations use POST and retain CSRF protection.

Run checks:

```bash
./mvnw clean test
```


## Student skill selection and self-assessment (T4E7A67)

- The student profile page includes a quick-search selector populated only from active skills in the admin catalog. Already selected skills are excluded from the available options.
- A student can add a catalog skill and self-assess it from level 1 to 5. The UI explains every level:
  - 1 – Mới bắt đầu: knows basic concepts and needs guidance.
  - 2 – Cơ bản: can complete simple tasks with documentation or guidance.
  - 3 – Trung bình: can independently handle routine tasks and common problems.
  - 4 – Nâng cao: works independently on complex cases and can support others.
  - 5 – Thành thạo: has deep knowledge and can design solutions or mentor others.
- The selected skills are displayed under their catalog category. Students can update a rating or remove a skill from their own profile.
- Duplicate selection is prevented in the service and by the database unique key on `profile_skills(profile_id, skill_id)`. Skill ownership is always resolved from the authenticated principal; no profile ID is accepted from the request.
- Hidden catalog skills cannot be newly selected, but existing student associations remain visible and can still be removed.
- The existing V1 `profile_skills` table already stores `proficiency_level` (1–5); no new migration is required for this task.
- Routes: `POST /me/skills`, `POST /me/skills/{skillId}` (update level), and `POST /me/skills/{skillId}/remove`. They are protected by the existing `ROLE_STUDENT` rule for `/me/**` and CSRF.

Run checks:

```bash
./mvnw clean test
```
