# Лабораторная работа №7 — клиент-сервер с PostgreSQL и авторизацией

## Что изменилось относительно лаб. 6

1. Коллекция хранится в **PostgreSQL**, а не в файле.
2. `id` генерируется **средствами БД** (`BIGSERIAL` → sequence).
3. Коллекция в памяти обновляется **только при успешной** записи в БД.
4. Команды чтения работают **с памятью**, команды записи — **с БД**.
5. **Регистрация и авторизация** пользователей (логин + пароль).
6. Пароли хешируются **SHA-256**, в БД хранится только хеш.
7. Неавторизованным пользователям команды **запрещены**.
8. У каждого объекта есть **владелец** (создатель).
9. Просмотр — всех объектов; модификация — **только своих**.
10. Логин и пароль отправляются **с каждым запросом**.
11. **Многопоточность**: Fixed thread pool (чтение), new Thread (обработка),
    ForkJoinPool (отправка), synchronized (синхронизация коллекции).

## Структура БД (создаётся автоматически при старте сервера)

```sql
CREATE TABLE users (
  login         VARCHAR(255) PRIMARY KEY,
  password_hash VARCHAR(64)  NOT NULL          -- SHA-256 (hex, 64 символа)
);

CREATE TABLE music_bands (
  id             BIGSERIAL PRIMARY KEY,          -- sequence
  name           VARCHAR(255) NOT NULL CHECK (length(name) > 0),
  coord_x        BIGINT NOT NULL CHECK (coord_x <= 52),
  coord_y        DOUBLE PRECISION NOT NULL,
  creation_date  TIMESTAMP NOT NULL,
  participants   BIGINT NOT NULL CHECK (participants > 0),
  genre          VARCHAR(64) NOT NULL,
  studio_name    VARCHAR(255),
  studio_address VARCHAR(255),
  owner_login    VARCHAR(255) NOT NULL REFERENCES users(login)
);
```

## Конфигурация БД

Реквизиты подключения хранятся в **отдельном файле** (не в репозитории).
Создайте `db.cfg` (по образцу `db.cfg.example`):

```
db.url=jdbc:postgresql://pg:5432/studs
db.user=s504568
db.password=ВАШ_ПАРОЛЬ
```

## Сборка

```
.\gradlew.bat :server:shadowJar :client:shadowJar
```

Получите:
- `server/build/libs/server-1.0.jar`
- `client/build/libs/client-1.0.jar`

(Postgres JDBC-драйвер уже включён в server jar через shadow.)

## Запуск на сервере (helios)

Загрузите через WinSCP в `~/lab7proga/`:
- `server.jar`, `client.jar`, `db.cfg`

Сервер:
```
java -jar server.jar 2222 db.cfg
```

Клиент:
```
java -jar client.jar localhost 2222
```

## Локальный тест с PostgreSQL (Windows/Linux/Mac)

1. Установите PostgreSQL, создайте БД:
   ```
   createdb studs
   ```
2. `db.cfg`:
   ```
   db.url=jdbc:postgresql://localhost:5432/studs
   db.user=postgres
   db.password=postgres
   ```
3. Запустите сервер и клиент как выше (host = localhost).

## Сценарий проверки

```
[client] 2  (register)  login: alice  password: pass1
[client] add ...        → объект создан, owner=alice
[client] exit

[client] 2  (register)  login: bob  password: pass2
[client] show           → bob видит объект alice
[client] remove_by_id 1 → Access denied (чужой объект)
[client] add ...        → создаётся объект bob
[client] remove_by_id 2 → удаляется (свой)
```

## Обработка ошибок (защита от «поломки»)

- Неверный логин/пароль → "Invalid login or password".
- Команда без авторизации → "Authorization required".
- Чужой объект на update/remove → "Access denied".
- Несуществующий id → "does not exist".
- Битый/нечитаемый конфиг БД → понятное сообщение, выход.
- Недоступность БД → команда возвращает ошибку, память не повреждается.
- Недоступность сервера → клиент делает 3 повтора (из лаб. 6).
- Некорректные данные объекта → валидация в моделях (IllegalArgumentException).
- SQL-инъекции → все запросы через PreparedStatement.
