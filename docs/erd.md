# T4E7A5C — ERD và thiết kế quan hệ cơ sở dữ liệu

> **Trạng thái:** Đề xuất thiết kế — chờ Mentor review/approve trên Pull Request.  
> **Phạm vi task:** Thiết kế ERD, giải thích khóa ngoại và index chính.  
> **Chưa thực hiện:** Không tạo Flyway migration, không tạo Entity/Repository và không thay đổi schema MySQL trước khi Mentor duyệt.

## 1. Mục tiêu và quy ước thiết kế

Thiết kế phục vụ hệ thống tuyển dụng với ba vai trò: Candidate, Recruiter và Admin. ERD gồm 13 bảng theo yêu cầu:

1. `users`
2. `profiles`
3. `educations`
4. `skills`
5. `profile_skills`
6. `projects`
7. `certificates`
8. `companies`
9. `shortlists`
10. `interview_requests`
11. `profile_reviews`
12. `notifications`
13. `profile_views`

Quy ước chung:
- Dùng khóa chính số nguyên lớn (`BIGINT`, tăng tự động) cho các bảng nghiệp vụ.
- Thời gian lưu theo `DATETIME`/UTC ở tầng ứng dụng; thống nhất timezone khi triển khai.
- Mật khẩu chỉ lưu dạng hash mạnh (ví dụ BCrypt), tuyệt đối không lưu plaintext.
- Các trạng thái và vai trò lưu dạng `VARCHAR` có kiểm soát ở tầng ứng dụng hoặc CHECK constraint nếu phiên bản MySQL hỗ trợ.
- Các bảng có `created_at`, `updated_at` phù hợp; bảng lịch sử có thêm `created_at` và không sửa/xóa bản ghi tùy tiện.
- Tài khoản bị khóa dùng `is_locked`; không xóa vật lý tài khoản thông thường khi còn dữ liệu lịch sử.

## 2. ERD — Mermaid

Có thể xem sơ đồ trực tiếp bằng Mermaid preview trên GitHub hoặc VS Code extension Mermaid.

```mermaid
erDiagram
    COMPANIES ||--o{ USERS : employs
    USERS ||--o| PROFILES : owns
    PROFILES ||--o{ EDUCATIONS : has
    PROFILES ||--o{ PROJECTS : has
    PROFILES ||--o{ CERTIFICATES : earns
    PROFILES ||--o{ PROFILE_SKILLS : maps
    SKILLS ||--o{ PROFILE_SKILLS : assigned
    USERS ||--o{ SHORTLISTS : creates
    PROFILES ||--o{ SHORTLISTS : shortlisted
    USERS ||--o{ INTERVIEW_REQUESTS : sends
    PROFILES ||--o{ INTERVIEW_REQUESTS : receives
    USERS ||--o{ PROFILE_REVIEWS : reviews
    USERS ||--o{ PROFILE_REVIEWS : is_reviewed
    PROFILES o|--o{ PROFILE_REVIEWS : concerns
    USERS ||--o{ NOTIFICATIONS : receives
    USERS ||--o{ PROFILE_VIEWS : views
    PROFILES ||--o{ PROFILE_VIEWS : viewed

    COMPANIES {
        BIGINT id PK
        VARCHAR name
        VARCHAR website
        VARCHAR address
        VARCHAR contact_email
        DATETIME created_at
        DATETIME updated_at
    }
    USERS {
        BIGINT id PK
        BIGINT company_id FK
        VARCHAR email UK
        VARCHAR password_hash
        VARCHAR role
        BOOLEAN is_locked
        VARCHAR account_status
        DATETIME created_at
        DATETIME updated_at
    }
    PROFILES {
        BIGINT id PK
        BIGINT user_id FK_UK
        VARCHAR full_name
        VARCHAR phone
        VARCHAR location
        TEXT summary
        VARCHAR headline
        BOOLEAN is_public
        DATETIME created_at
        DATETIME updated_at
    }
    EDUCATIONS {
        BIGINT id PK
        BIGINT profile_id FK
        VARCHAR institution
        VARCHAR degree
        VARCHAR major
        DATE start_date
        DATE end_date
        TEXT description
    }
    SKILLS {
        BIGINT id PK
        VARCHAR name UK
        DATETIME created_at
    }
    PROFILE_SKILLS {
        BIGINT id PK
        BIGINT profile_id FK
        BIGINT skill_id FK
        TINYINT proficiency_level
        DATETIME created_at
    }
    PROJECTS {
        BIGINT id PK
        BIGINT profile_id FK
        VARCHAR name
        TEXT description
        VARCHAR project_url
        DATE start_date
        DATE end_date
    }
    CERTIFICATES {
        BIGINT id PK
        BIGINT profile_id FK
        VARCHAR name
        VARCHAR issuer
        VARCHAR credential_code
        VARCHAR credential_url
        DATE issued_at
        DATE expires_at
    }
    SHORTLISTS {
        BIGINT id PK
        BIGINT recruiter_user_id FK
        BIGINT profile_id FK
        VARCHAR note
        DATETIME created_at
    }
    INTERVIEW_REQUESTS {
        BIGINT id PK
        BIGINT recruiter_user_id FK
        BIGINT profile_id FK
        VARCHAR job_title
        VARCHAR interview_type
        DATETIME proposed_at
        VARCHAR location_or_meeting_url
        TEXT message
        VARCHAR status
        DATETIME responded_at
        DATETIME created_at
        DATETIME updated_at
    }
    PROFILE_REVIEWS {
        BIGINT id PK
        BIGINT target_user_id FK
        BIGINT reviewer_user_id FK
        BIGINT profile_id FK
        VARCHAR decision
        TEXT reason
        DATETIME reviewed_at
    }
    NOTIFICATIONS {
        BIGINT id PK
        BIGINT recipient_user_id FK
        VARCHAR type
        VARCHAR title
        TEXT message
        VARCHAR target_url
        DATETIME read_at
        DATETIME created_at
    }
    PROFILE_VIEWS {
        BIGINT id PK
        BIGINT viewer_user_id FK
        BIGINT profile_id FK
        DATETIME viewed_at
    }
```

## 3. Giải thích từng bảng và quan hệ

### 3.1. Nhóm tài khoản và hồ sơ

| Bảng | Vai trò | Quan hệ / ràng buộc quan trọng |
|---|---|---|
| `companies` | Thông tin công ty tuyển dụng | Một công ty có nhiều user; Recruiter có thể liên kết công ty qua `users.company_id` |
| `users` | Đăng nhập, phân quyền, trạng thái tài khoản | `email` UNIQUE; `company_id` FK nullable; role gồm CANDIDATE/RECRUITER/ADMIN; `is_locked` |
| `profiles` | Hồ sơ nghề nghiệp của Candidate | `user_id` UNIQUE + FK → users.id; bảo đảm mỗi user có tối đa một profile |

**Lưu ý:** `account_status` biểu diễn vòng đời xét duyệt (PENDING/ACTIVE/REJECTED/SUSPENDED), còn `is_locked` là cờ khóa truy cập tức thời. Hai trường không thay thế nhau. Có thể cân nhắc bỏ `SUSPENDED` khỏi account_status nếu dự án muốn chỉ dùng is_locked cho khóa tạm thời; cần Mentor thống nhất.

### 3.2. Nhóm nội dung hồ sơ

| Bảng | Vai trò | Quan hệ / ràng buộc quan trọng |
|---|---|---|
| `educations` | Nhiều quá trình học của một profile | `profile_id` FK → profiles.id |
| `projects` | Dự án, sản phẩm hoặc kinh nghiệm nổi bật | `profile_id` FK → profiles.id |
| `certificates` | Chứng chỉ của ứng viên | `profile_id` FK → profiles.id |
| `skills` | Danh mục kỹ năng dùng chung | `name` UNIQUE để tránh kỹ năng trùng |
| `profile_skills` | Bảng nối profile và skill | Hai FK; UNIQUE(profile_id, skill_id); `proficiency_level` chỉ từ 1 đến 5 |

**Vì sao cần `profile_skills`?** Một profile có nhiều skill và một skill xuất hiện trong nhiều profile. Đây là quan hệ N-N, cần bảng nối. Mức độ thành thạo là thuộc tính của quan hệ giữa một profile cụ thể và một skill, vì vậy đặt ở `profile_skills`, không đặt ở `skills`.

### 3.3. Nhóm tuyển dụng và tương tác

| Bảng | Vai trò | Quan hệ / ràng buộc quan trọng |
|---|---|---|
| `shortlists` | Danh sách ứng viên Recruiter lưu lại | FK recruiter → users; FK profile → profiles; UNIQUE(recruiter_user_id, profile_id) |
| `interview_requests` | Lời mời phỏng vấn | FK Recruiter và Candidate profile; status PENDING/ACCEPTED/DECLINED/CANCELLED/EXPIRED |
| `profile_views` | Lịch sử xem hồ sơ | FK người xem → users; FK hồ sơ được xem → profiles |
| `notifications` | Thông báo cho người dùng | FK recipient → users; `read_at` NULL nghĩa là chưa đọc |

### 3.4. Lịch sử kiểm duyệt

`profile_reviews` lưu từng lần Admin duyệt/từ chối hồ sơ hoặc tài khoản:

- `target_user_id`: tài khoản bị xét duyệt.
- `reviewer_user_id`: Admin thực hiện quyết định.
- `profile_id`: hồ sơ liên quan, cho phép NULL nếu chỉ xét duyệt tài khoản trước khi profile được tạo.
- `decision`: PENDING không cần lưu thành review; các bản ghi lịch sử nên lưu APPROVED/REJECTED hoặc các quyết định được nghiệp vụ cho phép.
- `reason`: lý do từ chối hoặc ghi chú kiểm duyệt.
- `reviewed_at`: thời điểm ra quyết định.

Mỗi lần ra quyết định tạo một bản ghi mới để giữ lịch sử; không ghi đè quyết định cũ. Chỉ Admin được tạo quyết định review ở tầng service/security.

## 4. Giải thích khóa ngoại (Foreign Keys)

| FK | Ý nghĩa | Quy tắc xóa đề xuất |
|---|---|---|
| `users.company_id → companies.id` | User thuộc công ty | SET NULL khi công ty bị xóa nếu nghiệp vụ cho phép; ưu tiên soft-delete công ty |
| `profiles.user_id → users.id` | Profile thuộc một user | CASCADE khi xóa user chỉ khi đã xác nhận không cần giữ hồ sơ; mặc định nên soft-delete user |
| `educations.profile_id → profiles.id` | Education thuộc profile | CASCADE |
| `projects.profile_id → profiles.id` | Project thuộc profile | CASCADE |
| `certificates.profile_id → profiles.id` | Certificate thuộc profile | CASCADE |
| `profile_skills.profile_id → profiles.id` | Dòng kỹ năng thuộc profile | CASCADE |
| `profile_skills.skill_id → skills.id` | Dòng kỹ năng tham chiếu danh mục skill | RESTRICT nếu skill đang được sử dụng |
| `shortlists.recruiter_user_id → users.id` | Người lưu shortlist | CASCADE khi xóa dữ liệu shortlist theo user; soft-delete user là lựa chọn mặc định |
| `shortlists.profile_id → profiles.id` | Ứng viên được lưu | CASCADE hoặc soft-delete theo chính sách dữ liệu |
| `interview_requests.recruiter_user_id → users.id` | Recruiter gửi lời mời | RESTRICT/soft-delete để bảo toàn lịch sử tuyển dụng |
| `interview_requests.profile_id → profiles.id` | Ứng viên nhận lời mời | RESTRICT/soft-delete để bảo toàn lịch sử |
| `profile_reviews.target_user_id → users.id` | Tài khoản được xét duyệt | RESTRICT, giữ lịch sử kiểm duyệt |
| `profile_reviews.reviewer_user_id → users.id` | Admin thực hiện review | RESTRICT, bảo toàn người thực hiện |
| `profile_reviews.profile_id → profiles.id` | Profile liên quan | SET NULL nếu profile bị xóa nhưng vẫn cần giữ lịch sử |
| `notifications.recipient_user_id → users.id` | Chủ sở hữu thông báo | CASCADE hoặc chính sách lưu trữ theo yêu cầu |
| `profile_views.viewer_user_id → users.id` | Người xem hồ sơ | RESTRICT hoặc ẩn danh hóa theo chính sách quyền riêng tư |
| `profile_views.profile_id → profiles.id` | Hồ sơ được xem | CASCADE hoặc ẩn danh hóa theo chính sách lưu trữ |

**Nguyên tắc:** Không nên xóa vật lý users trong hoạt động bình thường. Dùng `is_locked` hoặc trạng thái vô hiệu hóa để giữ tính toàn vẹn tham chiếu, lịch sử phỏng vấn và lịch sử kiểm duyệt. Quy tắc CASCADE/RESTRICT cuối cùng cần được Mentor xác nhận theo chính sách lưu trữ dữ liệu.

## 5. Index và UNIQUE quan trọng

Index giúp truy vấn nhanh hơn nhưng làm tăng chi phí ghi/cập nhật. Chỉ tạo index cho cột thường xuyên dùng trong WHERE, JOIN, ORDER BY hoặc ràng buộc duy nhất.

| Bảng | Index / Unique đề xuất | Mục đích |
|---|---|---|
| `users` | UNIQUE(`email`) | Đăng nhập, ngăn email trùng |
| `users` | INDEX(`role`, `account_status`, `is_locked`) | Admin lọc tài khoản theo vai trò/trạng thái |
| `users` | INDEX(`company_id`) | Tìm user thuộc công ty; hỗ trợ FK |
| `profiles` | UNIQUE(`user_id`) | Một profile cho một user; tra profile từ user |
| `profiles` | INDEX(`is_public`, `location`) | Lọc hồ sơ công khai theo địa điểm |
| `educations` | INDEX(`profile_id`) | Lấy học vấn theo profile |
| `projects` | INDEX(`profile_id`) | Lấy dự án theo profile |
| `certificates` | INDEX(`profile_id`) | Lấy chứng chỉ theo profile |
| `skills` | UNIQUE(`name`) | Tránh tên skill trùng; tra skill |
| `profile_skills` | UNIQUE(`profile_id`, `skill_id`) | Không gán cùng skill hai lần cho một profile |
| `profile_skills` | INDEX(`skill_id`, `proficiency_level`, `profile_id`) | Tìm profile theo skill và mức thành thạo |
| `shortlists` | UNIQUE(`recruiter_user_id`, `profile_id`) | Không lưu trùng ứng viên trong shortlist của một Recruiter |
| `shortlists` | INDEX(`profile_id`) | Tra các shortlist có chứa profile |
| `interview_requests` | INDEX(`recruiter_user_id`, `status`, `created_at`) | Recruiter xem yêu cầu theo trạng thái và thời gian |
| `interview_requests` | INDEX(`profile_id`, `status`, `proposed_at`) | Candidate xem lời mời theo trạng thái/lịch |
| `profile_reviews` | INDEX(`target_user_id`, `reviewed_at`) | Xem lịch sử kiểm duyệt của tài khoản |
| `profile_reviews` | INDEX(`reviewer_user_id`, `reviewed_at`) | Truy vết hoạt động của Admin |
| `notifications` | INDEX(`recipient_user_id`, `read_at`, `created_at`) | Lấy thông báo chưa đọc/mới nhất |
| `profile_views` | INDEX(`profile_id`, `viewed_at`) | Thống kê lượt xem hồ sơ |
| `profile_views` | INDEX(`viewer_user_id`, `viewed_at`) | Tra lịch sử hồ sơ đã xem |

### Lưu ý về index

- MySQL InnoDB yêu cầu index cho FK; khi tạo FK, MySQL có thể tự tạo index cần thiết nếu chưa có index phù hợp. Vẫn cần rà soát index thực tế để tránh tạo trùng.
- UNIQUE index vừa bảo đảm dữ liệu không trùng vừa phục vụ tìm kiếm.
- Với `users.email`, nên chuẩn hóa email về lowercase ở service và dùng collation phù hợp để tránh hai email chỉ khác chữ hoa/chữ thường.
- Không nên index mọi cột: ví dụ `TEXT` như summary/message không nên tạo B-tree index thông thường nếu chưa có nhu cầu tìm kiếm cụ thể. Tìm kiếm full-text có thể là task riêng.
- Nếu bảng `profile_views` tăng rất nhanh, cần giới hạn thời gian lưu, tổng hợp dữ liệu hoặc partition ở giai đoạn tối ưu sau.

## 6. Ràng buộc dữ liệu đề xuất

1. `users.email` NOT NULL và UNIQUE.
2. `users.password_hash` NOT NULL; không trả trường này qua API/DTO.
3. `users.role` chỉ nhận CANDIDATE, RECRUITER, ADMIN.
4. `users.account_status` chỉ nhận PENDING, ACTIVE, REJECTED, SUSPENDED theo quy tắc đã thống nhất.
5. `profiles.user_id` NOT NULL và UNIQUE.
6. `profile_skills.proficiency_level` NOT NULL, từ 1 đến 5 (CHECK hoặc validation ở service).
7. `shortlists` không trùng cặp recruiter/profile.
8. `interview_requests.proposed_at` phải là thời điểm hợp lệ; thời gian tương lai được kiểm tra ở service.
9. `profile_reviews.reviewer_user_id` phải là Admin đang hoạt động; kiểm tra quyền ở service/security.
10. `profile_views` chỉ được ghi nhận khi người xem có quyền truy cập profile theo visibility/privacy policy.

## 7. Luồng dữ liệu chính

- Đăng ký: tạo `users` ở trạng thái PENDING; sau khi duyệt có thể tạo/kích hoạt `profiles` cho Candidate.
- Cập nhật hồ sơ: cập nhật `profiles`, thêm/sửa/xóa các dòng `educations`, `projects`, `certificates`, `profile_skills`.
- Tìm ứng viên: lọc `profiles` công khai, JOIN `profile_skills` và `skills`; chỉ trả dữ liệu được phép chia sẻ.
- Lưu ứng viên: thêm một dòng `shortlists` theo cặp Recruiter/profile.
- Mời phỏng vấn: tạo `interview_requests`; khi Candidate phản hồi cập nhật status và tạo `notifications` cho bên liên quan.
- Duyệt hồ sơ/tài khoản: cập nhật trạng thái hiện tại ở `users` và/hoặc `profiles`, đồng thời INSERT bản ghi mới vào `profile_reviews`.

## 8. Các điểm cần Mentor xác nhận trước migration

- [ ] Candidate có được tạo profile ngay khi đăng ký ở trạng thái PENDING hay chỉ sau khi Admin approve?
- [ ] Recruiter có bắt buộc thuộc một `company` không? Một công ty có nhiều Recruiter hay một Recruiter có thể thuộc nhiều công ty?
- [ ] Admin duyệt tài khoản, hồ sơ, hay cả hai? `profile_reviews` cần lưu loại đối tượng/loại quyết định riêng không?
- [ ] Dùng `account_status` cùng `is_locked` như đề xuất hay gộp trạng thái khóa vào account_status?
- [ ] Recruiter có được xem CV/thông tin liên hệ của mọi Candidate hay phải có consent/visibility cụ thể?
- [ ] Có cho phép nhiều yêu cầu phỏng vấn PENDING cho cùng Recruiter/profile không?
- [ ] Chính sách xóa dữ liệu: soft-delete, retention của notifications/profile_views, và FK RESTRICT/CASCADE/SET NULL.
- [ ] Chuẩn hóa tên cột, kiểu ID, timezone, collation và quy ước audit fields.

## 9. Điều kiện hoàn tất task

- ERD bao gồm đủ 13 bảng và thể hiện rõ cardinality.
- Các FK, UNIQUE và index chính được giải thích.
- Mentor review và approve PR.
- **Chỉ sau khi PR được approve mới bắt đầu task migration Flyway**; migration phải theo schema đã được duyệt, không tự ý đổi thiết kế trong lúc triển khai.

---

**Phạm vi hiện tại chỉ là tài liệu thiết kế. Chưa có file migration SQL nào được tạo.**
