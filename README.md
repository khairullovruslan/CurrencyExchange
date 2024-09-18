# Проект “CurrencyExchange”

## Описание

Проект представляет собой **REST API** для управления валютами и обменными курсами. Он позволяет просматривать и редактировать списки валют и обменных курсов, а также производить расчёт конвертации сумм из одной валюты в другую. **Веб-интерфейс** для проекта не предусмотрен.


## Функциональные возможности

- Получение списка валют.
- Получение информации о конкретной валюте.
- Добавление новой валюты.
- Получение списка обменных курсов.
- Получение информации о конкретном обменном курсе.
- Добавление нового обменного курса.
- Обновление существующего обменного курса.
- Расчёт конвертации между валютами.

## Технологии

- **Java** (коллекции, ООП)
- **Java Servlets** для реализации сервера
- **REST API** с использованием JSON
- **PostgreSQL** для хранения данных
- **Flyway** - для управления миграциями базы данных
- **JDBC** для работы с базой данных
- **Maven** для управления зависимостями

## Установка

### Условия

- Установленный **Java JDK** 8 или выше.
- **Apache Tomcat** 9 или выше.
- **PostgreSQL** для работы с базой данных.




## HTTP Запросы

### Валюты

#### Получение списка валют
- **Запрос:** `GET /currencies`
- **Описание:** Получает список всех валют.
- **Пример ответа:**
  ```json
  [
      {
          "id": 0,
          "name": "United States dollar",
          "code": "USD",
          "sign": "$"
      },
      {
          "id": 1,
          "name": "Euro",
          "code": "EUR",
          "sign": "€"
      }
  ]
- **200** — Успех
- **500** — Ошибка сервера

### Получение информации о конкретной валюте
- **Запрос:**  `GET /currency/{code}`
- **Описание:** Получает информацию о валюте по её коду.
- **Пример ответа:**
```json

{
    "id": 1,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
}
```
- **200** — Успех
- **400** — Код валюты отсутствует в адресе
- **404** — Валюта не найдена
- **500** — Ошибка сервера

### Добавление новой валюты
- **Запрос:**  `POST /currencies`
- **Описание:** Добавляет новую валюту в базу данных.
- **Данные:** Передаются в теле запроса в формате x-www-form-urlencoded (поля: name, code, sign).
- **Пример ответа:**

```json
{
    "id": 2,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
}
```
- **201** — Успех
- **400** — Отсутствует нужное поле формы
- **409** — Валюта с таким кодом уже существует
- **500** — Ошибка сервера
  
### Получение списка всех обменных курсов
- **Запрос:**  `GET /exchangeRates`
- **Описание:** Получает список всех обменных курсов.
- **Пример ответа:**

```json
[
    {
        "id": 0,
        "baseCurrency": {
            "id": 0,
            "name": "United States dollar",
            "code": "USD",
            "sign": "$"
        },
        "targetCurrency": {
            "id": 1,
            "name": "Euro",
            "code": "EUR",
            "sign": "€"
        },
        "rate": 0.99
    }
]
```
- **200** — Успех
- **500** — Ошибка сервера

### Получение конкретного обменного курса
- **Запрос:**  `GET /exchangeRate/USDRUB `
- **Описание:** Получение конкретного обменного курса
- **Пример ответа:**

```json
{
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
}
```
- **200** — Успех
- **400** — Коды валюты отсутствуют в адресе
- **404** — Обменный курс не найден
- **500** — Ошибка сервера


### Добавление нового обменного курса
- **Запрос:**  `POST /exchangeRates`
- **Описание:**  Добавляет новый обменный курс в базу.
- **Данные:** Передаются в теле запроса в формате x-www-form-urlencoded (поля: baseCurrencyCode, targetCurrencyCode, rate).
- **Пример ответа:**

```json


{
    "id": 1,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
}
```
- **201** — Успех
- **400** — Отсутствует нужное поле формы
- **404** — Одна (или обе) валюта из валютной пары не существует
- **409** — Валютная пара с таким кодом уже существует
- **500** — Ошибка сервера

### Обновление существующего обменного курса
- **Запрос:**  `PATCH /exchangeRate/{baseCurrencyCode}{targetCurrencyCode}`
- **Описание:**  Обновляет курс обмена для заданной валютной пары.
- **Данные:** Передаются в теле запроса в формате x-www-form-urlencoded (единственное поле: rate).
- **Пример ответа:**

```json

{
    "id": 1,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
}
```
- **200** — Успех
- **400** — Отсутствует нужное поле формы
- **404** — Одна (или обе) валюта из валютной пары не существует
- **409** — Валютная пара с таким кодом уже существует
- **500** — Ошибка сервера

### Расчет перевода между валютами
- **Запрос:**  `GET /exchange?from={baseCurrencyCode}&to={targetCurrencyCode}&amount={amount}`
- **Описание:** Рассчитывает конвертацию заданной суммы из одной валюты в другую.
- **Пример запроса:** `GET /exchange?from=USD&to=AUD&amount=10`
- **Пример ответа:**
```json
{
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Australian dollar",
        "code": "AUD",
        "sign": "A$"
    },
    "rate": 1.45,
    "amount": 10.00,
    "convertedAmount": 14.50
}
```
- **Сценарии расчета:**
  - 1. Используется курс из таблицы ExchangeRates для заданной валютной пары
  - 2. Используется обратный курс, если задана другая пара.

- **200** — Успех
- **400** — Неверные параметры запроса
- **404** — Валюта не найдена
- **500** — Ошибка сервера



### Клонирование репозитория

```bash
git clone https://github.com/khairullovruslan/CurrencyExchange.git
cd https://github.com/khairullovruslan/CurrencyExchange.git



