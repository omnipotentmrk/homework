API 호출 방법
-
* POST /spread-money/event
```
//Request
curl --location --request POST 'http://localhost:8080/spread-money/event' \
--header 'X-USER-ID: 사용자 ID' \
--header 'X-ROOM-ID: 대화방 ID' \
--header 'Content-Type: application/json' \
--data-raw '{"targetMemberCount":5,"totalAmount":"1234"}' //targetMemberCount 은 분배할 인원, totalAmount 는 뿌릴 돈을 의미

//Response
{
    "token": "tp*"
}
```
* GET /spread-money/receive
```
//Request
curl --location --request GET 'http://localhost:8080/spread-money/receive' \
--header 'X-USER-ID: 사용자 ID' \
--header 'X-ROOM-ID: 대화방 ID' \
--header 'token: &J5'

//Response
{
    "amount": 239
}
```
* GET /spread-money/status
```
//Request
curl --location --request GET 'http://localhost:8080/spread-money/status' \
--header 'X-USER-ID: 사용자 ID' \
--header 'X-ROOM-ID: 대화방 ID' \
--header 'token: &J5'

//Response
{
    "registeredTime": "2020-08-09 15:31:35",
    "currency": "KRW",
    "totalSpreadAmount": 1234,
    "totalReceivedAmount": 1234,
    "receivedInfo": [
        {
            "userId": 2,
            "receivedAmount": 54
        },
        {
            "userId": 3,
            "receivedAmount": 799
        },
        {
            "userId": 4,
            "receivedAmount": 59
        },
        {
            "userId": 5,
            "receivedAmount": 15
        },
        {
            "userId": 6,
            "receivedAmount": 307
        }
    ]
}
```
* 에러 반환시
```
{
    "errorCode": foo,
    "errorMessage": bar
}
```

DB 테이블, 함수 등 설정 방법
-
* MySQL 5.7 을 사용
* 테이블, 함수 등은 https://github.com/omnipotentmrk/homework/blob/master/db.sql 을 통해 확인 가능

토큰 발급 방법
-
* MySQL 에 랜덤한 문자열을 반환하는 함수를 생성
* MySQL 의 Trigger 를 이용하여 뿌리기 이벤트 값이 DB 에 INSERT 되기 전, 위에서 만든 함수를 호출해서 랜덤 문자열을 반환받아 함께 INSERT

분배 방법
-
* MySQL 에서 아직 수령하지 않은 돈의 정보가 담기 데이터를 RAND 함수를 이용해서 무작위 선정, 이를 사용자가 수령하게 끔 개발  