[![Build Status](https://travis-ci.org/heyferh/money-transfer.svg?branch=master)](https://travis-ci.org/heyferh/money-transfer)
### Purpose
1. RESTful API for money transfers between accounts
2. Simple and to the point
3. No Spring
4. For the sake of simplicity only 3 currencies are supported: **USD, EUR, RUB**
### Run tests
```
./gradlew test
```
### Run server at localhost:4567
```
./gradlew run
```
### API usage
* ```GET /account ```
* ```GET /account/{accountId}```
* ```GET /transfer/{accountId} ```
* ```POST /account ```
##### Sample request to create an account
    {
      "money": {
          "amount": "1000",
          "currency": "RUB"
      }
    }
##### Sample response
    {
      "id": 1,
      "money": {
          "amount": 1000,
          "currency": "RUB"
      }
    } 
* ```POST /transfer ```
##### Sample request to transfer money between two accounts
    {
      "fromId": "2",
      "toId": "1",
      "money": {
           "amount": "5",
           "currency": "EUR"
        }
    }
##### Sample response
    {
       "fromId": "2",
       "toId": "1",
       "money": {
            "amount": "5",
            "currency": "EUR"
        }
    }

### Technologies
1. **Spark**
2. **H2 in-memory mode**
3. **JPA / Hibernate**
4. **Dagger 2**
5. **Gradle**
6. **Jackson**
7. **Logback**
8. **JUnit**
