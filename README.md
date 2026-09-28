# Hospital Management

Website / API quản lý bệnh viện được xây dựng bằng Java Spring Boot và SQL Server.

## 1. Mục tiêu project

Project này cung cấp backend cho hệ thống quản lý bệnh viện, bao gồm:

- Đăng nhập / đăng ký người dùng
- Quản lý khoa bệnh viện
- Kết nối tới SQL Server qua Spring Data JPA
- Cấu hình bảo mật bằng Spring Security

## 2. Những thứ cần chuẩn bị

### Yêu cầu phần cứng / môi trường

- Java 21
- Maven 3.9+ hoặc sử dụng Maven Wrapper có sẵn trong repo
- SQL Server 2019/2022 (local hoặc Docker)
- Git

### Cài đặt Java 21

- macOS: dùng SDKMAN, Homebrew hoặc cài thủ công JDK 21
- Windows: cài JDK 21 từ Oracle / Adoptium
- Linux: cài OpenJDK 21

Kiểm tra:

```bash
java -version
```

Kết quả mong muốn:

```bash
openjdk version "21.x"
```

### Cài đặt SQL Server

Bạn cần một database tên `QuanLyBenhVien` hoặc đổi tên trong file cấu hình để phù hợp với database của bạn.

Nếu bạn muốn chạy bằng Docker, có thể dùng lệnh sau:

```bash
docker run -e "ACCEPT_EULA=Y" -e "MSSQL_SA_PASSWORD=YourStrong!Passw0rd" \
  -p 1433:1433 --name hospital-sql \
  -d mcr.microsoft.com/mssql/server:2022-latest
```

Sau khi container chạy, tạo database:

```sql
CREATE DATABASE QuanLyBenhVien;
GO
```

Lưu ý: Repo hiện tại đang giả định bảng và dữ liệu đã tồn tại trong SQL Server. Nếu database chưa có schema, bạn cần tạo bảng tương ứng trước khi chạy app.

## 3. Cấu hình project

File cấu hình chính nằm ở:

```text
src/main/resources/application.properties
```

Ví dụ cấu hình cần thiết:

```properties
server.port=8080

spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=QuanLyBenhVien;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=YourStrong!Passw0rd
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

Lưu ý:

- Trong repo hiện tại, dòng `spring.datasource.password` cần được điền đúng mật khẩu SQL Server của bạn.
- Nếu dùng SQL Server khác, hãy chỉnh `spring.datasource.url`, `username`, `password` phù hợp.

## 4. Cách chạy project

### 4.1. Dùng Maven Wrapper (khuyến nghị)

Tại thư mục gốc của project:

```bash
chmod +x mvnw
./mvnw clean package
```

Nếu trên Windows:

```powershell
mvnw.cmd clean package
```

### 4.2. Chạy ứng dụng

Sau khi build xong, chạy:

```bash
java -jar target/hospital-management-0.0.1-SNAPSHOT.jar
```

Hoặc chạy trực tiếp bằng Spring Boot:

```bash
./mvnw spring-boot:run
```

Ứng dụng sẽ chạy ở port:

```text
http://localhost:8080
```

## 5. Các endpoint chính

### Auth

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`

Ví dụ request login:

```json
{
  "username": "admin",
  "password": "123456"
}
```

### Khoa

- `GET /api/v1/khoa`

## 6. Test / build nhanh

### Build project

```bash
./mvnw clean compile
```

### Chạy test

```bash
./mvnw test
```

## 7. Các lưu ý quan trọng

- Project đang dùng Java 21 và Spring Boot 4.1.1.
- Cần có SQL Server đang chạy trước khi khởi động ứng dụng.
- Nếu database chưa có schema / bảng, app sẽ không hoạt động đúng chức năng.
- File `application.properties` cần được cập nhật với password thật của bạn.
- Bảng `NguoiDung` và `Khoa` đang mapping theo entity bằng JPA, vì vậy tên table và schema cần phù hợp với database.

## 8. Troubleshooting

### Lỗi không kết nối được SQL Server

- Kiểm tra SQL Server đã chạy chưa
- Kiểm tra cổng `1433`
- Kiểm tra `spring.datasource.url` và `password`
- Kiểm tra database `QuanLyBenhVien` có tồn tại không

### Lỗi port 8080 đang được sử dụng

Thay đổi trong file `application.properties`:

```properties
server.port=8081
```

### Lỗi Java version

Nếu `java -version` không phải Java 21, hãy cập nhật `JAVA_HOME` và thử lại:

```bash
export JAVA_HOME=/path/to/jdk-21
export PATH=$JAVA_HOME/bin:$PATH
```

## 9. Cấu trúc project chính

```text
hospital-management/
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  └─ resources/
│  └─ test/
├─ pom.xml
├─ mvnw
├─ mvnw.cmd
├─ README.md
└─ .mvn/
```

## 10. Gợi ý phát triển tiếp theo

- Thêm script tạo schema SQL cho database
- Thêm file `.env.example` để quản lý mật khẩu và cấu hình
- Thêm unit test / integration test
- Bổ sung Swagger / OpenAPI để test API dễ hơn

Nếu bạn muốn, tôi có thể tiếp tục viết cho bạn:

1. một phiên bản README ngắn gọn hơn cho GitHub, hoặc
2. một README chuyên nghiệp hơn theo chuẩn dự án với mục tiêu deploy lên server.
