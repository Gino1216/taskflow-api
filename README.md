# TaskFlow API

REST API quản lý công việc cá nhân, tập trung vào những kỹ năng phù hợp với vị trí Thực tập sinh/Lập trình viên Java mới ra trường: thiết kế API, Spring Security, JWT, JPA, validation, xử lý lỗi tập trung và kiểm thử tích hợp.

## Điểm nổi bật

- Đăng ký/đăng nhập, mã hóa mật khẩu bằng BCrypt và cấp JWT được ký bằng HMAC-SHA256.
- CRUD công việc; mỗi người dùng chỉ có thể đọc, sửa hoặc xóa dữ liệu của chính mình.
- Lọc theo trạng thái, phân trang và sắp xếp kết quả.
- Chạy nhanh với H2; có profile PostgreSQL để triển khai trong môi trường thực tế.
- Kiểm thử luồng đăng ký → tạo công việc → truy vấn công việc bằng MockMvc.

## Chạy trên máy cá nhân

Yêu cầu: JDK 21 và Maven 3.9 trở lên.

```bash
mvn spring-boot:run
```

Ứng dụng chạy tại `http://localhost:8080`. Giao diện H2 Console nằm tại `http://localhost:8080/h2-console`, với JDBC URL là `jdbc:h2:mem:taskflow`.

### Chạy với PostgreSQL

```bash
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

Trong môi trường thực tế, hãy đặt biến `APP_JWT_SECRET` thành một chuỗi bí mật dài ít nhất 32 byte.

## Thử nghiệm API

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"intern@example.com","password":"password123","displayName":"Java Intern"}'
```

Lấy giá trị `accessToken` trong phản hồi, sau đó thực hiện:

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"title":"Học Spring Security","description":"Hoàn thành luồng JWT","dueDate":"2030-12-31"}'

curl -H "Authorization: Bearer <TOKEN>" "http://localhost:8080/api/tasks?status=TODO&page=0&size=10"
```

## API

| Phương thức | Endpoint | Mô tả |
|---|---|---|
| POST | `/api/auth/register` | Đăng ký và nhận JWT |
| POST | `/api/auth/login` | Đăng nhập |
| GET | `/api/tasks` | Danh sách công việc của người dùng |
| GET | `/api/tasks/{id}` | Chi tiết công việc |
| POST | `/api/tasks` | Tạo công việc |
| PUT | `/api/tasks/{id}` | Cập nhật công việc |
| DELETE | `/api/tasks/{id}` | Xóa công việc |

## Kiểm thử

```bash
mvn test
```

## Trao đổi khi phỏng vấn

### Vì sao API không trả trực tiếp JPA entity?

API sử dụng DTO để không làm lộ cấu trúc bảng, tránh vòng lặp khi tuần tự hóa quan hệ JPA và cho phép thay đổi database mà ít ảnh hưởng đến hợp đồng API. Bean Validation cũng được đặt trên request DTO để kiểm tra dữ liệu ngay tại biên hệ thống.

### Làm thế nào để người dùng chỉ truy cập được công việc của mình?

JWT cung cấp email của người dùng đã xác thực. Tầng service tìm task bằng cả `taskId` và `ownerId`, thay vì chỉ kiểm tra quyền tại controller. Vì vậy một người dùng không thể đọc, sửa hoặc xóa task của người khác bằng cách thay ID trên URL.

### Mật khẩu và JWT được bảo vệ như thế nào?

Mật khẩu được băm một chiều bằng BCrypt và không bao giờ được trả về API. JWT được ký bằng HMAC-SHA256, có thời hạn hai giờ và được kiểm tra ở mọi endpoint cần xác thực. Khi triển khai thật, khóa ký được truyền qua biến môi trường `APP_JWT_SECRET`.

### Vì sao service dùng `@Transactional`?

Các thao tác ghi được thực hiện trong một transaction để thay đổi dữ liệu có tính nguyên tử. Các truy vấn chỉ đọc dùng `@Transactional(readOnly = true)` nhằm thể hiện rõ ý định và cho phép JPA tối ưu phù hợp.

### Nếu phát triển tiếp, dự án nên bổ sung gì?

- Flyway hoặc Liquibase để quản lý phiên bản schema thay cho `ddl-auto=update`.
- Refresh token, thu hồi token và giới hạn số lần đăng nhập sai.
- Testcontainers để kiểm thử tích hợp trực tiếp với PostgreSQL.
- OpenAPI/Swagger, logging có cấu trúc và metrics phục vụ vận hành.

## Mô tả ngắn để đưa vào CV

**TaskFlow - Personal Task Management API**  
Xây dựng REST API quản lý công việc bằng Java 21, Spring Boot, Spring Security, JWT và PostgreSQL. Thiết kế phân quyền dữ liệu theo người dùng, validation, phân trang, xử lý ngoại lệ tập trung và kiểm thử tích hợp với JUnit/MockMvc. Đóng gói cơ sở dữ liệu bằng Docker Compose.

