# T4E7A5B — User stories và tiêu chí chấp nhận

## 1. Phạm vi và vai trò

Tài liệu mô tả nền tảng tuyển dụng, nơi ứng viên tạo hồ sơ và tìm cơ hội, nhà tuyển dụng quản lý ứng viên và yêu cầu phỏng vấn, quản trị viên quản lý tài khoản và kiểm duyệt đăng ký.

| Vai trò | Mô tả |
|---|---|
| Ứng viên (Candidate) | Người tìm việc, quản lý hồ sơ cá nhân và ứng tuyển/tương tác với nhà tuyển dụng |
| Nhà tuyển dụng (Recruiter) | Người tìm kiếm, xem hồ sơ ứng viên và gửi yêu cầu phỏng vấn |
| Quản trị viên (Admin) | Người quản lý tài khoản, xét duyệt đăng ký và kiểm soát quyền truy cập |

## 2. User stories và acceptance criteria

### A. Ứng viên (Candidate)

#### US-C01 — Đăng ký tài khoản
**Là** ứng viên, **tôi muốn** tạo tài khoản bằng họ tên, email và mật khẩu, **để** sử dụng hệ thống tuyển dụng.

**Tiêu chí chấp nhận**
- Given tôi đang ở màn hình đăng ký, When nhập đầy đủ dữ liệu hợp lệ và gửi form, Then hệ thống tạo tài khoản ở trạng thái chờ duyệt (Pending).
- Email phải đúng định dạng và chưa được sử dụng.
- Mật khẩu phải đáp ứng chính sách độ dài/độ phức tạp; trường xác nhận mật khẩu phải trùng khớp.
- Khi dữ liệu không hợp lệ, hệ thống hiển thị lỗi cạnh trường tương ứng và không tạo tài khoản.

#### US-C02 — Đăng nhập
**Là** ứng viên, **tôi muốn** đăng nhập bằng email và mật khẩu, **để** truy cập hồ sơ cá nhân.

**Tiêu chí chấp nhận**
- Given tài khoản đã được duyệt và đang hoạt động, When thông tin đăng nhập chính xác, Then tôi được chuyển đến trang phù hợp với vai trò.
- Nếu sai email/mật khẩu, hệ thống hiển thị thông báo chung, không tiết lộ tài khoản nào tồn tại.
- Tài khoản Pending, bị từ chối hoặc bị khóa không được đăng nhập; hệ thống thông báo trạng thái phù hợp.
- Sau khi đăng xuất, phiên đăng nhập được kết thúc.

#### US-C03 — Xem hồ sơ của tôi
**Là** ứng viên, **tôi muốn** xem hồ sơ cá nhân, **để** kiểm tra thông tin mà nhà tuyển dụng có thể sử dụng.

**Tiêu chí chấp nhận**
- Trang hồ sơ hiển thị họ tên, email, số điện thoại, địa chỉ, kỹ năng, kinh nghiệm, học vấn và liên kết CV (nếu có).
- Chỉ chủ hồ sơ và người dùng có quyền phù hợp mới xem được thông tin riêng tư.
- Nếu chưa có dữ liệu tùy chọn, hệ thống hiển thị trạng thái “Chưa cập nhật”, không hiển thị lỗi.

#### US-C04 — Cập nhật hồ sơ
**Là** ứng viên, **tôi muốn** chỉnh sửa thông tin hồ sơ, **để** giữ thông tin tìm việc luôn chính xác.

**Tiêu chí chấp nhận**
- Tôi có thể cập nhật các trường được phép chỉnh sửa và lưu thay đổi.
- Email đăng nhập không được thay đổi trực tiếp nếu chưa qua quy trình xác minh.
- Dữ liệu bắt buộc và định dạng (email, số điện thoại, URL) được kiểm tra trước khi lưu.
- Sau khi lưu thành công, dữ liệu mới được hiển thị trên trang hồ sơ.

#### US-C05 — Quản lý CV
**Là** ứng viên, **tôi muốn** tải lên hoặc thay CV, **để** nhà tuyển dụng xem được kinh nghiệm của tôi.

**Tiêu chí chấp nhận**
- Chỉ chấp nhận định dạng PDF (theo giới hạn dung lượng do hệ thống cấu hình).
- Tệp không hợp lệ hoặc vượt dung lượng bị từ chối và có thông báo rõ ràng.
- Khi tải CV mới thành công, hệ thống cập nhật tệp hiện tại và hiển thị tên/ngày tải lên.
- CV chỉ được cung cấp cho người dùng đã xác thực và có quyền xem.

#### US-C06 — Theo dõi yêu cầu phỏng vấn
**Là** ứng viên, **tôi muốn** xem các lời mời phỏng vấn, **để** biết lịch và phản hồi nhà tuyển dụng.

**Tiêu chí chấp nhận**
- Danh sách hiển thị nhà tuyển dụng, vị trí, thời gian đề xuất, trạng thái và ghi chú.
- Tôi có thể chấp nhận hoặc từ chối yêu cầu còn hiệu lực.
- Khi phản hồi, trạng thái được cập nhật và nhà tuyển dụng có thể xem kết quả.
- Yêu cầu đã hết hạn hoặc đã phản hồi không thể được phản hồi lần nữa.

### B. Nhà tuyển dụng (Recruiter)

#### US-R01 — Truy cập khu vực nhà tuyển dụng
**Là** nhà tuyển dụng, **tôi muốn** đăng nhập bằng tài khoản được cấp quyền, **để** sử dụng chức năng tuyển dụng.

**Tiêu chí chấp nhận**
- Chỉ tài khoản đã được Admin duyệt và đang hoạt động mới truy cập được.
- Người dùng Candidate không thể truy cập URL dành riêng cho Recruiter.
- Khi không đủ quyền, hệ thống trả về trang 403 hoặc chuyển đến trang thông báo không có quyền.

#### US-R02 — Tìm kiếm ứng viên
**Là** nhà tuyển dụng, **tôi muốn** tìm ứng viên theo từ khóa và tiêu chí, **để** nhanh chóng tìm người phù hợp.

**Tiêu chí chấp nhận**
- Có thể tìm theo tên/kỹ năng và lọc theo kinh nghiệm, địa điểm hoặc kỹ năng.
- Kết quả chỉ bao gồm hồ sơ được phép hiển thị và phù hợp điều kiện tìm kiếm.
- Khi không có kết quả, hệ thống hiển thị trạng thái rỗng và gợi ý thay đổi bộ lọc.
- Bộ lọc có thể kết hợp; thao tác xóa bộ lọc đưa danh sách về mặc định.

#### US-R03 — Xem chi tiết ứng viên
**Là** nhà tuyển dụng, **tôi muốn** xem chi tiết hồ sơ ứng viên, **để** đánh giá mức độ phù hợp với vị trí.

**Tiêu chí chấp nhận**
- Trang chi tiết hiển thị các thông tin được ứng viên cho phép chia sẻ: kỹ năng, kinh nghiệm, học vấn, CV.
- Thông tin nhạy cảm không được hiển thị nếu Recruiter không có quyền.
- Có đường dẫn quay lại danh sách tìm kiếm và giữ các điều kiện lọc trước đó.
- Nếu hồ sơ không tồn tại hoặc không còn quyền truy cập, hệ thống hiển thị thông báo phù hợp.

#### US-R04 — Gửi yêu cầu phỏng vấn
**Là** nhà tuyển dụng, **tôi muốn** gửi lời mời phỏng vấn cho ứng viên, **để** bắt đầu quy trình trao đổi tuyển dụng.

**Tiêu chí chấp nhận**
- Form yêu cầu có vị trí, hình thức, thời gian đề xuất và nội dung lời mời.
- Các trường bắt buộc phải hợp lệ; thời gian đề xuất phải ở tương lai.
- Khi gửi thành công, yêu cầu được tạo với trạng thái Pending và gắn đúng ứng viên/nhà tuyển dụng.
- Hệ thống ngăn gửi trùng yêu cầu đang chờ cho cùng ứng viên và vị trí (nếu quy tắc nghiệp vụ bật).

#### US-R05 — Quản lý yêu cầu phỏng vấn
**Là** nhà tuyển dụng, **tôi muốn** xem và theo dõi các yêu cầu phỏng vấn đã gửi, **để** quản lý tiến độ trao đổi.

**Tiêu chí chấp nhận**
- Danh sách có thể lọc theo trạng thái và sắp xếp theo ngày tạo/thời gian phỏng vấn.
- Mỗi yêu cầu hiển thị ứng viên, vị trí, thời gian và trạng thái mới nhất.
- Nhà tuyển dụng có thể hủy yêu cầu còn Pending; yêu cầu đã được phản hồi không thể hủy theo luồng này.
- Chi tiết yêu cầu hiển thị lịch sử cập nhật trạng thái.

#### US-R06 — Cập nhật hồ sơ nhà tuyển dụng
**Là** nhà tuyển dụng, **tôi muốn** cập nhật thông tin liên hệ và công ty, **để** ứng viên nhận biết đơn vị tuyển dụng.

**Tiêu chí chấp nhận**
- Có thể chỉnh sửa tên hiển thị, công ty, website và thông tin liên hệ được phép.
- Website phải là URL hợp lệ; các trường bắt buộc không được để trống.
- Sau khi lưu, thông tin mới xuất hiện ở các yêu cầu phỏng vấn liên quan.
- Không thể tự nâng quyền hoặc thay đổi trạng thái duyệt của tài khoản.

### C. Quản trị viên (Admin)

#### US-A01 — Đăng nhập quản trị
**Là** quản trị viên, **tôi muốn** đăng nhập vào khu vực quản trị, **để** quản lý hệ thống an toàn.

**Tiêu chí chấp nhận**
- Chỉ tài khoản có role ADMIN và trạng thái ACTIVE mới truy cập được.
- Tài khoản không có role ADMIN bị từ chối dù đăng nhập thành công ở khu vực người dùng.
- Các trang quản trị được bảo vệ ở cả tầng giao diện và tầng xử lý request.

#### US-A02 — Quản trị tài khoản
**Là** quản trị viên, **tôi muốn** xem danh sách và trạng thái tài khoản, **để** quản lý người dùng hệ thống.

**Tiêu chí chấp nhận**
- Danh sách hiển thị tên, email, vai trò, trạng thái và ngày tạo.
- Có thể tìm theo tên/email và lọc theo vai trò/trạng thái.
- Danh sách có phân trang; bộ lọc được giữ khi chuyển trang.
- Mật khẩu hoặc thông tin xác thực không bao giờ xuất hiện trong danh sách/chi tiết.

#### US-A03 — Duyệt tài khoản đăng ký
**Là** quản trị viên, **tôi muốn** xem hàng chờ đăng ký và duyệt/từ chối tài khoản, **để** chỉ người dùng hợp lệ được sử dụng hệ thống.

**Tiêu chí chấp nhận**
- Hàng chờ chỉ hiển thị tài khoản có trạng thái Pending.
- Admin có thể mở thông tin đăng ký, chọn Approve hoặc Reject và nhập lý do khi từ chối.
- Approve chuyển trạng thái sang ACTIVE; Reject chuyển sang REJECTED.
- Tài khoản đã xử lý không thể được duyệt/từ chối lặp lại từ dữ liệu cũ; hệ thống kiểm tra trạng thái mới nhất.

#### US-A04 — Khóa và mở khóa tài khoản
**Là** quản trị viên, **tôi muốn** khóa hoặc mở khóa tài khoản, **để** xử lý hành vi vi phạm hoặc khôi phục quyền truy cập.

**Tiêu chí chấp nhận**
- Admin có thể khóa tài khoản ACTIVE và mở khóa tài khoản SUSPENDED theo quyền được cấp.
- Tài khoản bị khóa không thể tạo phiên đăng nhập mới.
- Hành động yêu cầu xác nhận và lưu người thực hiện, thời điểm, lý do.
- Admin không thể vô hiệu hóa tài khoản quản trị cuối cùng đang hoạt động.

#### US-A05 — Quản lý vai trò
**Là** quản trị viên, **tôi muốn** gán vai trò phù hợp cho tài khoản, **để** phân quyền đúng trách nhiệm.

**Tiêu chí chấp nhận**
- Chỉ Admin có quyền quản lý vai trò mới thấy và sử dụng chức năng này.
- Vai trò chỉ được chọn từ danh sách hợp lệ (ADMIN, RECRUITER, CANDIDATE).
- Thay đổi vai trò được ghi nhận lịch sử; người dùng không thể tự thay đổi vai trò.
- Hệ thống yêu cầu xác nhận trước khi áp dụng thay đổi quyền.

#### US-A06 — Theo dõi lịch sử kiểm duyệt
**Là** quản trị viên, **tôi muốn** xem lịch sử duyệt/từ chối/khóa tài khoản, **để** kiểm tra và truy vết các quyết định quản trị.

**Tiêu chí chấp nhận**
- Lịch sử hiển thị đối tượng, hành động, người thực hiện, thời điểm và lý do.
- Có thể lọc theo hành động, tài khoản và khoảng thời gian.
- Người dùng thông thường không thể truy cập dữ liệu audit.
- Bản ghi lịch sử không bị sửa/xóa qua giao diện quản trị thông thường.

## 3. Quy tắc nghiệp vụ và giả định

- Trạng thái tài khoản đề xuất: `PENDING`, `ACTIVE`, `REJECTED`, `SUSPENDED`.
- Vai trò đề xuất: `CANDIDATE`, `RECRUITER`, `ADMIN`.
- Trạng thái yêu cầu phỏng vấn đề xuất: `PENDING`, `ACCEPTED`, `DECLINED`, `CANCELLED`, `EXPIRED`.
- Đăng ký công khai mặc định tạo tài khoản Candidate ở trạng thái Pending; tài khoản Recruiter/Admin được tạo hoặc cấp quyền theo quy trình quản trị.
- Mọi chức năng phải kiểm tra quyền ở server-side; ẩn nút trên giao diện không được xem là cơ chế phân quyền.
- Các tiêu chí về dung lượng CV, độ dài mật khẩu và thời hạn phản hồi cần được chốt trong cấu hình/nghiệp vụ trước khi triển khai.

## 4. Definition of Done cho user story

- Luồng chính và luồng lỗi được kiểm thử.
- Validation được thực hiện ở server-side.
- Phân quyền được kiểm tra ở server-side.
- Thông báo thành công/thất bại rõ ràng.
- Không để lộ mật khẩu, token hoặc dữ liệu riêng tư trong log/response.
- Tài liệu và kiểm thử được cập nhật khi thay đổi nghiệp vụ.
