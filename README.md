# Домашнее задания по предмету "СИРНЯ JAVA"

Сервис для генерации и проверки одноразовых паролей (OTP) с доставкой через Email, SMS, Telegram и файл.

---

## Описание

Сервис предоставляет API для:
- Регистрации и аутентификации пользователей (JWT токены)
- Генерации OTP-кодов с настраиваемой длиной и временем жизни
- Доставки кодов через 4 канала: Email, SMS, Telegram, файл
- Валидации OTP-кодов с проверкой срока действия
- Администрирования (управление пользователями и настройками)

**Роли пользователей:**
- `ADMIN` — полный доступ к управлению
- `USER` — базовый доступ к OTP-операциям

---

## Пример использования сервиса:

### 1. Регистрация пользователя
```bash
curl -X POST http://localhost:8080/api/auth/**register** \
  -H "Content-Type: application/json" \
  -d '{"login":"username","password":"pass123","role":"USER","email":"user@example.com"}'
```

### 2. Вход в систему (получение JWT токена)
```bash
curl -X POST http://localhost:8080/api/auth/**login** \
  -H "Content-Type: application/json" \
  -d '{"login":"username","password":"pass123"}'
```

### 3. Генерация OTP кода
```bash
curl -X POST http://localhost:8080/api/otp/**generate** \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"operationId":"payment-123","channel":"EMAIL"}'
```

### 4. Проверка OTP кода
```bash
curl -X POST http://localhost:8080/api/otp/**validate** \
  -H "Authorization: Bearer <JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"operationId":"payment-123","code":"123456"}'
```

## Очень важно:

### 1. Чтобы не был занят порт 8080

### 2. Правильный ввод пользовательских данных для БД (Postgres) в файле application.properties
