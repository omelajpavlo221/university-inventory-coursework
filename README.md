# Інформаційна система видачі та повернення університетського майна (Варіант №15)

Курсова робота з дисципліни **«Кросплатформене програмування»**.  
Студент: **Омелай Павло Володимирович**, група **201-ТН**.

## Технологічний стек
- **Java 21**, **Maven**
- **MySQL 8.x** + **JDBC** (чистий рівень доступу `DAO` із `PreparedStatement`)
- **Java Servlet API 4.0** (пакет `javax.servlet`, Apache Tomcat 9.x) + **Jackson** (`HTTP/JSON`)
- **JavaFX 21** (графічний інтерфейс користувача, фоновий `Task` із `ProgressBar`)
- **JUnit 5** (12 модульних тестів предметної логіки)

## Структура проєкту
```text
university-inventory-coursework/
├── docs/screenshots/          # Ілюстрації роботи системи та тестування
├── database.sql               # SQL-скрипт створення БД та демонстраційних даних
├── README.md                  # Інструкція зі складання та запуску
├── .gitignore                 # Виключення target/, *.war, IDE та логів
├── server/                    # Веб-сервер (WAR для Apache Tomcat)
│   ├── pom.xml
│   └── src/
│       ├── main/java/ua/edu/nupp/kpp/server/
│       └── test/java/ua/edu/nupp/kpp/server/CourseworkTest.java
└── client/                    # Настільний клієнт JavaFX
    ├── pom.xml
    ├── sample-data/inventory.csv
    └── src/main/java/ua/edu/nupp/kpp/client/
```

## Порядок підготовки та запуску

### 1. Підготовка бази даних MySQL
1. Запустіть MySQL (через XAMPP або системну службу).
2. Відкрийте phpMyAdmin або MySQL Workbench та виконайте скрипт `database.sql`.
3. Переконайтеся, що створено базу `university_inventory` з таблицями `asset`, `app_user`, `issue_request`, `transfer_record`.

### 2. Тестування та складання серверної частини
У терміналі перейдіть до каталогу `server` та виконайте:
```bash
mvn clean test
mvn clean package
```
Очікуваний результат: 12 тестів JUnit 5 виконано успішно (`Failures: 0, Errors: 0`), у `server/target/` згенеровано файл `university-inventory-server.war`.

### 3. Розгортання в Apache Tomcat
1. Скопіюйте `server/target/university-inventory-server.war` у каталог `webapps/` сервера Apache Tomcat 9.
2. Запустіть Tomcat (`bin/startup.bat` або `bin/startup.sh`).
3. Перевірте працездатність у браузері: `http://localhost:8080/university-inventory-server/assets`.

### 4. Запуск JavaFX-клієнта
У терміналі перейдіть до каталогу `client` та виконайте:
```bash
mvn javafx:run
```

## Формат CSV-файлу для фонового імпорту
Відносний шлях: `sample-data/inventory.csv` (кодування UTF-8, роздільник `;`).
```csv
inv_number;name;category;available_qty;criticality_score
INV-2001;Маршрутизатор MikroTik RB4011;Мережеве обладнання;3;85.0
```



```bash
mvn clean test
```


У `database.sql` навмисно залишено два початкові записи `issue_request`.
Рисунок 6.1 показує стан після демонстрації, коли через застосунок було
створено ще дві заявки. Аналогічно, шість записів `asset` у демонстраційній
базі утворюються з трьох початкових записів SQL та трьох рядків CSV-імпорту.
