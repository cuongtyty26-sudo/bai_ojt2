# T4E7A5B — Wireframes các màn hình chính

## 1. Quy ước

- Đây là wireframe mức low-fidelity, tập trung vào bố cục và thành phần tương tác, chưa phải thiết kế màu sắc/UI hoàn chỉnh.
- Ký hiệu `[ ... ]` là nút hoặc trường nhập; `( )` là radio/checkbox; `{ ... }` là vùng nội dung.
- Header điều hướng thay đổi theo vai trò và chỉ hiển thị chức năng người dùng có quyền.
- Các màn hình dưới đây tương ứng với yêu cầu: đăng nhập, đăng ký, hồ sơ của tôi, quản trị tài khoản, hàng chờ duyệt, tìm ứng viên, chi tiết ứng viên, yêu cầu phỏng vấn.

## 2. Đăng nhập — Login

```text
+--------------------------------------------------------------+
| LOGO / Recruitment Portal                         [Trang chủ] |
+--------------------------------------------------------------+
|                                                              |
|                    ĐĂNG NHẬP                                 |
|                                                              |
|              Email                                           |
|              [________________________________]              |
|              Mật khẩu                                        |
|              [________________________________] [Hiện]       |
|                                                              |
|              [ ] Ghi nhớ đăng nhập                           |
|                                                              |
|              [             ĐĂNG NHẬP             ]            |
|                                                              |
|              Chưa có tài khoản? [Đăng ký]                    |
|              [Quên mật khẩu?]                                 |
|                                                              |
|              {Thông báo lỗi / trạng thái tài khoản}          |
+--------------------------------------------------------------+
```

**Thành phần/ hành vi**
- Email, mật khẩu, checkbox ghi nhớ, nút đăng nhập, liên kết đăng ký/quên mật khẩu.
- Lỗi xác thực hiển thị gần form; không tiết lộ email có tồn tại hay không.
- Tài khoản Pending/Rejected/Suspended hiển thị hướng dẫn trạng thái phù hợp.

## 3. Đăng ký — Register

```text
+--------------------------------------------------------------+
| LOGO / Recruitment Portal                                    |
+--------------------------------------------------------------+
|                       TẠO TÀI KHOẢN                           |
|                                                              |
|  Họ và tên *             [____________________________]      |
|  Email *                 [____________________________]      |
|  Số điện thoại           [____________________________]      |
|  Mật khẩu *              [____________________________]      |
|  Xác nhận mật khẩu *     [____________________________]      |
|                                                              |
|  Vai trò:  (•) Ứng viên   ( ) Nhà tuyển dụng                  |
|  {Nếu đăng ký Recruiter: thông tin công ty / website}        |
|                                                              |
|  [ ] Tôi đồng ý với điều khoản sử dụng                        |
|                                                              |
|  [                 ĐĂNG KÝ                    ]               |
|  Đã có tài khoản? [Đăng nhập]                                 |
|  {Lỗi validation / thông báo gửi đăng ký thành công}         |
+--------------------------------------------------------------+
```

**Thành phần/ hành vi**
- Trường bắt buộc được đánh dấu *; lỗi validation hiển thị cạnh trường.
- Theo giả định nghiệp vụ hiện tại, đăng ký công khai tạo Candidate Pending; nếu cho phép đăng ký Recruiter thì vẫn phải chờ Admin duyệt.
- Không cho phép người dùng tự chọn ADMIN.

## 4. Hồ sơ của tôi — My Profile

```text
+-----------------------------------------------------------------------+
| LOGO | Trang chủ | Hồ sơ của tôi | Yêu cầu phỏng vấn | [Tài khoản v] |
+-----------------------------------------------------------------------+
| HỒ SƠ CỦA TÔI                                      [Chỉnh sửa hồ sơ]  |
|                                                                       |
|  +----------------------+  +----------------------------------------+ |
|  |      [Ảnh đại diện]  |  | Nguyễn Văn A                           | |
|  |                      |  | Email: user@example.com                | |
|  |   [Đổi ảnh]          |  | Điện thoại: 09xxxxxxxx                 | |
|  |                      |  | Địa điểm: Tokyo                        | |
|  +----------------------+  +----------------------------------------+ |
|                                                                       |
|  KỸ NĂNG                                                              |
|  [Java] [Spring Boot] [MySQL] [+ Thêm]                                |
|                                                                       |
|  KINH NGHIỆM                                                          |
|  +-----------------------------------------------------------------+  |
|  | Vị trí / Công ty / Thời gian                                    |  |
|  | Mô tả công việc                                                 |  |
|  +-----------------------------------------------------------------+  |
|                                                                       |
|  HỌC VẤN                                                              |
|  +-----------------------------------------------------------------+  |
|  | Trường / Chuyên ngành / Thời gian                               |  |
|  +-----------------------------------------------------------------+  |
|                                                                       |
|  CV: resume.pdf  | Tải lên [Chọn tệp] [Cập nhật CV] [Xem CV]          |
|  [Lưu thay đổi]   [Hủy]                                               |
+-----------------------------------------------------------------------+
```

**Thành phần/ hành vi**
- Chia nhóm thông tin cá nhân, kỹ năng, kinh nghiệm, học vấn và CV.
- Nút chỉnh sửa bật form; Lưu thay đổi chạy validation và thông báo kết quả.
- Trường chưa có dữ liệu hiển thị “Chưa cập nhật”.

## 5. Quản trị tài khoản — Admin / Account Management

```text
+----------------------------------------------------------------------------+
| LOGO | ADMIN CONSOLE | Tổng quan | Tài khoản | Hàng chờ duyệt | [Admin v] |
+----------------------------------------------------------------------------+
| QUẢN TRỊ TÀI KHOẢN                                                        |
|                                                                            |
| [Tìm tên hoặc email________________] [Vai trò v] [Trạng thái v] [Tìm]     |
|                                                                            |
| +-----------------------------------------------------------------------+  |
| | Họ tên       | Email             | Vai trò    | Trạng thái | Thao tác  |  |
| |--------------|-------------------|------------|------------|-----------|  |
| | Nguyễn A     | a@example.com     | CANDIDATE  | ACTIVE     | [Xem] [...]| |
| | Trần B       | b@example.com     | RECRUITER  | PENDING    | [Xem] [...]| |
| | Lê C         | c@example.com     | CANDIDATE  | SUSPENDED  | [Xem] [...]| |
| +-----------------------------------------------------------------------+  |
|                                                                            |
|  [< Trước]                 Trang 1 / 10                 [Sau >]             |
|                                                                            |
|  {Modal xác nhận: Khóa/Mở khóa/Gán vai trò + lý do}                       |
+----------------------------------------------------------------------------+
```

**Thành phần/ hành vi**
- Tìm kiếm, lọc vai trò/trạng thái, bảng tài khoản và phân trang.
- Menu thao tác phụ thuộc quyền: xem, khóa/mở khóa, gán vai trò.
- Các hành động ảnh hưởng quyền truy cập phải có bước xác nhận; mật khẩu không được hiển thị.

## 6. Hàng chờ duyệt — Approval Queue

```text
+----------------------------------------------------------------------------+
| LOGO | ADMIN CONSOLE | Tổng quan | Tài khoản | Hàng chờ duyệt | [Admin v] |
+----------------------------------------------------------------------------+
| HÀNG CHỜ DUYỆT                                      Pending: 12           |
|                                                                            |
| [Tìm tên/email____________________] [Loại tài khoản v] [Ngày gửi v] [Tìm] |
|                                                                            |
| +-----------------------------------------------------------------------+  |
| | Người đăng ký | Email          | Loại       | Ngày gửi  | Thao tác    |  |
| |---------------|----------------|------------|-----------|-------------|  |
| | Nguyễn A      | a@example.com  | Candidate  | 20/11/26  | [Xem]       |  |
| | Trần B        | b@example.com  | Recruiter   | 21/11/26  | [Xem]       |  |
| +-----------------------------------------------------------------------+  |
|                                                                            |
| {Panel chi tiết khi chọn một yêu cầu}                                      |
|  Họ tên / Email / Số điện thoại / Vai trò / Thông tin công ty             |
|  Tài liệu liên quan: [Xem]                                                 |
|  Lý do từ chối (bắt buộc khi Reject): [____________________________]      |
|  [TỪ CHỐI]                                           [DUYỆT TÀI KHOẢN]    |
+----------------------------------------------------------------------------+
```

**Thành phần/ hành vi**
- Chỉ liệt kê tài khoản Pending; mở chi tiết ngay trong panel hoặc trang riêng.
- Approve yêu cầu xác nhận; Reject yêu cầu nhập lý do.
- Sau khi xử lý, bản ghi rời khỏi hàng chờ và trạng thái được cập nhật.

## 7. Tìm ứng viên — Candidate Search (Recruiter)

```text
+----------------------------------------------------------------------------+
| LOGO | Bảng điều khiển | Tìm ứng viên | Yêu cầu phỏng vấn | [Recruiter v] |
+----------------------------------------------------------------------------+
| TÌM KIẾM ỨNG VIÊN                                                         |
|                                                                            |
| Từ khóa (tên / kỹ năng) [___________________________] [TÌM KIẾM]          |
|                                                                            |
| BỘ LỌC                         | KẾT QUẢ (128)                            |
| +----------------------------+ | +--------------------------------------+ |
| | Địa điểm                   | | | Nguyễn Văn A          [Xem hồ sơ]     | |
| | [_______________________]  | | | Tokyo | 3 năm kinh nghiệm             | |
| | Kỹ năng                    | | | Java, Spring Boot, MySQL               | |
| | [ ] Java  [ ] SQL          | | +--------------------------------------+ |
| | [ ] Spring [ ] Python      | | | Trần Thị B            [Xem hồ sơ]     | |
| | Kinh nghiệm                | | | Osaka | 5 năm kinh nghiệm             | |
| | [Từ __] đến [__] năm       | | | Java, AWS, Docker                     | |
| | [Áp dụng] [Xóa lọc]        | | +--------------------------------------+ |
| +----------------------------+ |                                      |
|                                | [< Trước]   Trang 1 / 13   [Sau >]    |
+----------------------------------------------------------------------------+
```

**Thành phần/ hành vi**
- Tìm theo từ khóa và kết hợp bộ lọc kỹ năng, địa điểm, số năm kinh nghiệm.
- Mỗi kết quả chỉ hiển thị thông tin ứng viên được phép chia sẻ.
- Có trạng thái loading, không có kết quả và lỗi tải dữ liệu; phân trang giữ bộ lọc.

## 8. Chi tiết ứng viên — Candidate Detail (Recruiter)

```text
+----------------------------------------------------------------------------+
| LOGO | Bảng điều khiển | Tìm ứng viên | Yêu cầu phỏng vấn | [Recruiter v] |
+----------------------------------------------------------------------------+
| [< Quay lại kết quả]                                                       |
|                                                                            |
|  +---------------------+  Nguyễn Văn A                                    |
|  |   [Ảnh đại diện]    |  Java Developer                                  |
|  |                     |  Tokyo | 3 năm kinh nghiệm                       |
|  +---------------------+  [GỬI YÊU CẦU PHỎNG VẤN]                         |
|                                                                            |
|  GIỚI THIỆU                                                                |
|  {Tóm tắt mục tiêu nghề nghiệp và kinh nghiệm}                             |
|                                                                            |
|  KỸ NĂNG                                                                   |
|  [Java] [Spring Boot] [MySQL] [REST API]                                  |
|                                                                            |
|  KINH NGHIỆM                                                               |
|  +------------------------------------------------------------------------+|
|  | Công ty X — Java Developer — 2023 đến nay                             ||
|  | Mô tả vai trò, trách nhiệm và thành tựu                                ||
|  +------------------------------------------------------------------------+|
|                                                                            |
|  HỌC VẤN                                                                   |
|  {Trường / Bằng cấp / Chuyên ngành}                                        |
|                                                                            |
|  CV: [Xem / Tải CV] (chỉ khi được cấp quyền)                               |
+----------------------------------------------------------------------------+
```

**Thành phần/ hành vi**
- Hiển thị hồ sơ theo quyền chia sẻ; CV có kiểm soát quyền truy cập.
- Nút gửi yêu cầu phỏng vấn mở form và tự gắn ứng viên đang xem.
- Quay lại danh sách giữ nguyên từ khóa, bộ lọc và trang hiện tại.

## 9. Yêu cầu phỏng vấn — Interview Request

```text
+----------------------------------------------------------------------------+
| LOGO | Bảng điều khiển | Tìm ứng viên | Yêu cầu phỏng vấn | [Recruiter v] |
+----------------------------------------------------------------------------+
| TẠO YÊU CẦU PHỎNG VẤN                                                      |
|                                                                            |
| Ứng viên *          [Nguyễn Văn A                         v]               |
| Vị trí tuyển dụng * [Java Developer                       ]               |
| Hình thức *         (•) Online   ( ) Trực tiếp                             |
| Ngày phỏng vấn *    [dd/mm/yyyy]       Giờ * [hh:mm]                       |
| Địa điểm / Link     [___________________________________________]          |
| Người liên hệ       [___________________________________________]          |
| Nội dung lời mời *  [___________________________________________]          |
|                     [___________________________________________]          |
|                     [___________________________________________]          |
|                                                                            |
| {Thông báo validation: thời gian phải ở tương lai, trường bắt buộc...}    |
|                                                                            |
| [HỦY]                                      [GỬI YÊU CẦU PHỎNG VẤN]         |
+----------------------------------------------------------------------------+
| DANH SÁCH YÊU CẦU ĐÃ GỬI                                                   |
| [Trạng thái v] [Từ ngày] [Đến ngày] [Lọc]                                 |
| Ứng viên       | Vị trí          | Thời gian       | Trạng thái | [Chi tiết]|
| Nguyễn Văn A   | Java Developer  | 05/12/26 10:00  | PENDING    | [Xem]     |
+----------------------------------------------------------------------------+
```

**Thành phần/ hành vi**
- Form tạo lời mời và danh sách yêu cầu đã gửi có thể đặt cùng trang hoặc tách tab.
- Thời gian phải ở tương lai; hình thức online yêu cầu link họp, trực tiếp yêu cầu địa điểm.
- Gửi thành công tạo trạng thái Pending; hệ thống hiển thị thông báo và bản ghi mới.
- Nhà tuyển dụng có thể xem trạng thái phản hồi và hủy yêu cầu còn Pending.

## 10. Điều hướng và responsive

- Candidate: Trang chủ → Hồ sơ của tôi → Yêu cầu phỏng vấn.
- Recruiter: Bảng điều khiển → Tìm ứng viên → Chi tiết ứng viên → Gửi yêu cầu phỏng vấn → Theo dõi yêu cầu.
- Admin: Admin Console → Quản trị tài khoản → Hàng chờ duyệt → Xem chi tiết → Duyệt/Từ chối.
- Trên màn hình nhỏ, menu chuyển thành nút điều hướng; bảng dữ liệu chuyển thành danh sách card hoặc có cuộn ngang.
- Tất cả form cần có nhãn, trạng thái lỗi dễ nhận biết, focus keyboard và thông báo thao tác thành công/thất bại.

## 11. Liên kết user story với wireframe

| Màn hình | User stories liên quan |
|---|---|
| Đăng nhập | US-C02, US-R01, US-A01 |
| Đăng ký | US-C01 |
| Hồ sơ của tôi | US-C03, US-C04, US-C05, US-R06 |
| Quản trị tài khoản | US-A02, US-A04, US-A05, US-A06 |
| Hàng chờ duyệt | US-A03 |
| Tìm ứng viên | US-R02 |
| Chi tiết ứng viên | US-R03 |
| Yêu cầu phỏng vấn | US-C06, US-R04, US-R05 |
