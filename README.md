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

