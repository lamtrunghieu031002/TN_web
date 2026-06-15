# PTIT Check

Hệ thống chấm bài SQL: backend Spring Boot + frontend React (Vite/Tailwind) + MySQL.

## Stack
- **Backend**: Java 17, Spring Boot 3.4, Spring Security + JWT, JPA/Hibernate, MySQL 8
- **Frontend**: React 19, Vite, Tailwind, axios + react-router
- **Hạ tầng**: Docker Compose (mysql + backend + frontend nginx)

## Yêu cầu
- Docker + Docker Compose, hoặc:
- Java 17, Maven, Node 20+, MySQL 8 (chạy local)

## Cấu hình
```bash
cp .env.example .env
# Chỉnh các giá trị: mật khẩu DB, JWT_SECRET, mail App Password, admin password...
```
Các biến bắt buộc: `MYSQL_ROOT_PASSWORD`, `SPRING_DATASOURCE_*`, `JWT_SECRET` (>= 64 ký tự), `MAIL_USERNAME/PASSWORD`, `ADMIN_DEFAULT_PASSWORD`.

Sinh JWT secret nhanh:
```bash
openssl rand -base64 64
```

## Chạy bằng Docker
```bash
docker compose up --build
```
- Frontend: http://localhost
- Backend:  http://localhost:8080
- MySQL:    localhost:3307

## Chạy local (dev)
```bash
# Backend
cd ptit
./mvnw spring-boot:run

# Frontend (terminal khác)
cd frontend
npm install
npm run dev   # http://localhost:5173
```
Khi dev local nhớ set `VITE_API_BASE=http://localhost:8080` trong `.env`.

## Tài khoản admin mặc định
Lần đầu khởi động `DataInitializer` sẽ:
- Seed 3 role: `ROLE_STUDENT`, `ROLE_TEACHER`, `ROLE_ADMIN`
- Tạo user admin theo `ADMIN_DEFAULT_USERNAME` / `ADMIN_DEFAULT_PASSWORD`

→ Đăng nhập ngay vào `/admin` để tạo bài tập.

## Test
```bash
cd ptit
./mvnw test
```

## Cấu trúc
```
ptitcheck/
├── ptit/          # Backend Spring Boot
├── frontend/      # Frontend React
├── docker-compose.yml
├── .env.example
└── README.md
```

## API chính
| Method | Endpoint                      | Quyền             |
|--------|-------------------------------|-------------------|
| POST   | /api/auth/register            | public            |
| POST   | /api/auth/login               | public            |
| POST   | /api/auth/forgot-password     | public            |
| POST   | /api/auth/reset-password      | public            |
| GET    | /api/problems                 | đã đăng nhập      |
| POST   | /api/problems                 | ADMIN             |
| POST   | /api/submissions/submit       | đã đăng nhập      |
| GET    | /api/submissions/leaderboard  | đã đăng nhập      |
| GET    | /api/users/all                | ADMIN             |
| PUT    | /api/users/{id}/roles         | ADMIN             |
