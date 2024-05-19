## 🌱 중고 상품 플랫폼 Fresh Trash

--- 

<p align="center">
  <img width="130" alt="jujeol_og_image" src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/c59a7b43-f772-4898-ab0d-829c358570cf">
</p>
<p align="center">
  <img src="https://img.shields.io/badge/react-v18.2.0-9cf?logo=react" alt="react" />
  <img src="https://img.shields.io/badge/spring_boot-v2.7.18-green?logo=springboot"  alt="spring-boot" />
  <img src="https://img.shields.io/badge/mariadb-v10.11.7-blue?logo=mariadb" alt="mariadb"/>
  <img src="https://img.shields.io/badge/redis-v-red?logo=redis" alt="redis"/>
  <img src="https://img.shields.io/badge/docker-v-blue?logo=docker" alt="docker"/>
</p>

**Fresh Trash는 중고 상품을 판매, 구매 할 수 있는 온라인 플랫폼입니다.**
- Fresh Trash에 가입해서 집안에 안쓰고 자리만 차지했던 애물단지를 **등록**하고 **판매, 나눔**하세요.
- 다른 유저가 등록한 애물단지를 살펴보고 **구매**해 보세요.
- 마음에 드는 애물단지를 **찜**하세요.
- 애물단지에 대해 궁금한 점이 있으면 **실시간 채팅**으로 물어보세요.

## Project Architecture

--- 
<p>
   <img src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/20b3f24e-515e-454f-ae63-233924e15c5d" alt="architecture" />
</p>


## Tech Stacks

--- 

### Spring Framework

<p>
  <img src="https://user-images.githubusercontent.com/52682603/138834253-9bcd8b12-241f-41b2-85c4-d723a16bdb58.png" alt="spring_boot" width=15%>
  <img src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/dc38f77a-6ccd-465a-bf0f-904a56392394" alt="OAuth2" width=23%>
  <img src="https://user-images.githubusercontent.com/52682603/138834267-c86e4b93-d826-4fd4-bcc8-1294f615a82d.png" alt="hibernate" width=15%>
  <img src="https://user-images.githubusercontent.com/52682603/138834280-73acd37b-97ef-4136-b58e-6138eb4fcc46.png" alt="query_dsl" width=15%>
  <img src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/e8d1d412-1039-4c39-9e41-12742a9bd080" alt="websocket" width=15%>
</p>

- **Spring Data JPA(Hibernate)** 로 객체 지향 데이터 로직을 작성했습니다.
- **Spring Security(JWT)** 로그인 성공시 토큰을 발급합니다. 
- **OAuth2** 를 사용하여 구글, 네이버, 카카오를 통한 간편 로그인합니다.
- **Spring Mail(+ Email Validation API)** 를 사용하여 이메일을 인증합니다.
- **QueryDSL** 을 사용하여 목록 페이지에서 **카테고리, 제목, 주소(읍면동)** 으로 검색합니다.
- **Spring WebSocket(+ STOMP)** 로 거래관련 1:1 채팅을 합니다.
- **Spring AMQP(RabbitMQ)**, **SSE(Server-Sent Event)** 를 사용하여 거래 상태 변경시 알림을 보냅니다.


### Database
<p>
   <img width=15% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/a25f6bf9-3ee0-490b-a056-177f2d2674ef" alt="mariadb" />
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/7b72cc13-95d0-453b-b79b-fd5b010f80cd" alt="redis" />
</p>

- 데이터베이스는 MariaDB를 사용합니다.
- 유저정보캐싱, 인증코드 저장에 Redis를 사용합니다.


### AWS
<p>
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/8f095459-d55c-45a3-a5ed-bb696a2be8f3" alt="ec2" />
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/f74b475c-7f91-4e91-9e09-24a79101e84a" alt="s3" />
</p>

- **Amazon EC2** 로 서버를 구축했습니다.
- **Amazon S3** 를 사용하여 이미지를 저장, 관리합니다.
- **Amazon RDS** 에서 데이터베이스를 관리합니다.
- **Amazon ElastiCache** 로 메모리데이터 처리시간을 향상시킵니다.


### CI / CD
<p>
   <img width=13% src="image" src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/e80f1d7c-deff-4bc3-874d-cb2e587ff1b3" alt="github_action" />
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/2fa86757-9c7a-4ab1-90ce-8580bd3a5d9e" alt="code_deploy" />
</p>

- **GithubAction, CodeDeploy** 으로 빌드, 테스트 및 지속적 배포를 진행합니다.


## ERD

---
<p>
   <img src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/d6b96325-9f2d-4ca6-bde9-62533b2aef5e" alt="ERD" />
</p>


## 주요 기능

---
1. 로그인
   - `JWT` : 로그인 성공시 토큰 발급
   - `Redis` 캐싱 : 로그인 후 Redis에 사용자 정보를 캐싱하고 이후 API요청 시 캐싱한 정보를 사용
   - 일정 횟수 이상 신고받은 유저일 경우 로그인 실패 알림


2. 회원가입
   - 간편 가입 : `OAuth2.0`을 사용하여 구글, 네이버, 카카오를 통한 간편 로그인
   - 메일 검증 : `Email Validation API` 사용하여 메일 검증을 통해 유효한 메일인 경우만 발송
   - 비동기 처리 : 메일 발송을 비동기 처리하여 사용자가 정지된 화면에서 기다리는 시간을 감소시킴


3. 사용자 정보
   - 정보 수정 : 닉네임 중복 확인 후 이미지 파일이 유효하지 않을 경우에도 다른 정보들은 수정


4. 상품 등록/수정/삭제
   - 이미지 관리 : `Amazon S3` 사용하여 이미지 저장/관리
   - 파일 유효성 검증 : 상품 수정시 10byte보다 크고 [jpeg, jpg, png] 확장자 이미지 파일일 경우 수정


5. 상품 목록 
   - 검색 기능 : `QueryDSL`을 사용하여 애물단지 목록 페이지에서 **카테고리, 제목, 주소(읍면동)** 으로 검색할 수 있도록 구현
   - 페이징 처리 : `Pageable`을 이용한 페이징 처리


6. 상품 상세
   - 관심 추가/삭제 : 본인이 올린 물건을 제외한 상품에 관심 추가, 삭제 가능 


7. 알림 기능
   - SSE(Server-Sent-Events) : SSE는 별도의 프로토콜을 사용하지 않고 HTTP 프로토콜만으로 사용할 수 있으며 
                               알림은 서버 → 클라이언트 단방향 통신이므로 SSE사용하여 알림 구현 
   - Spring AMQP(RabbitMQ)를 사용한 pub/sub : 
     - 동시 접속자 수가 증가할 경우 알람 기능을 처리하기위한 서버의 부담이 증가하기 때문에 외부 Message Queue를 사용하여 개선
     - 메시지 전송에 실패할 경우 Spring AMQP 내부의 Retry 정책을 적용하여 1초 간격으로 3회 재요청하도록 설정


8. 1:1 채팅 기능 
   - WebSocket(STOMP) : 웹소켓만 이용할 경우 connection 별로 해당 메시지가 어떤 요청이고 어떻게 처리해야할지 등을 직접 구현해야하는 번거로움이 있습니다. 
                        반면 STOMP는 메시지의 형식, 유형, 내용 등을 정의해주는 프로토콜로 애플리케이션을 쉽게 구현할 수 있다는 장점이 있습니다. 
                        또한 외부 메시지 브로커를 사용할 수 있습니다.
   - 외부 브로커 RabbitMQ 사용 : Spring AMQP에서 내부 브로커를 사용할 경우 서버에 종속적이기 때문에 이후 서버가 증설될 경우
                             서버 간의 메시지 공유가 안되어서 외부 브로커를 적용했습니다. 또한 서버의 부담을 줄일 수 있다는 장점도 있습니다.
   