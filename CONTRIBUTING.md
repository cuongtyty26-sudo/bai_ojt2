# Quy ước làm việc nhóm (`bai_ojt2`)

Tài liệu này thống nhất quy trình Git, đặt tên nhánh, viết commit message, tạo Pull Request và review code cho toàn bộ nhóm.

---

## 1. Quy ước đặt tên nhánh (Branch Naming)

Luôn tạo nhánh mới từ `main` mới nhất theo định dạng:

- `feature/<mã-task>-<tên-ngắn>` — Tính năng mới (vd: `feature/t4e7a5a-init-project`)
- `fix/<mã-task>-<tên-ngắn>` — Sửa lỗi (vd: `fix/t4e7a5a-fix-login`)
- `docs/<mã-task>-<tên-ngắn>` — Tài liệu (vd: `docs/t4e7a5a-update-readme`)
- `chore/<mã-task>-<tên-ngắn>` — Cấu hình, bảo trì (vd: `chore/t4e7a5a-deps`)

> **Lưu ý:** Không push trực tiếp lên `main`. Tên nhánh viết thường, phân cách bằng dấu `-`.

---

## 2. Quy ước Commit Message

Áp dụng **Conventional Commits** kết hợp **Mã task** để commit tự động liên kết với task:

```text
<type>: <MÃ-TASK> <mô tả ngắn gọn>
```

Hoặc:

```text
<MÃ-TASK>: <type> <mô tả ngắn gọn>
```

Các `<type>` chuẩn:
- `feat`: Thêm tính năng mới
- `fix`: Sửa bug
- `docs`: Cập nhật tài liệu
- `style`: Định dạng code (không đổi logic)
- `refactor`: Tái cấu trúc code
- `test`: Thêm hoặc sửa unit/integration test
- `chore`: Cập nhật build, cấu hình, dependencies

**Ví dụ:**
- `feat: T4E7A5A thêm form đăng nhập`
- `T4E7A5A: thêm form đăng nhập`
- `fix: T4E7A5A sửa lỗi validate email`
- `docs: T4E7A5A cập nhật hướng dẫn chạy local`

---

## 3. Quy ước Pull Request (PR) & Review

1. **Tiêu đề PR** cần chứa mã task (ví dụ: `T4E7A5A: Khởi tạo dự án Spring Boot 3`).
2. Điền đầy đủ nội dung theo mẫu trong [.github/pull_request_template.md](.github/pull_request_template.md).
3. **Bắt buộc tối thiểu 1 approving review** trước khi merge vào `main`.
4. Sau khi merge xong, xóa nhánh feature đã hoàn thành.

### Thiết lập Branch Protection trên GitHub (dành cho Admin repo)
1. Vào **Settings** -> **Branches** -> **Add branch protection rule** (hoặc **Rulesets**).
2. Branch name pattern: `main`.
3. Bật **Require a pull request before merging**.
4. Bật **Require approvals** -> chọn số lượng tối thiểu là **1**.
5. Lưu cấu hình.
