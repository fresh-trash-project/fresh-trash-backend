## 🌱 중고 상품 플랫폼 Fresh Trash

> **로고 클릭시 노션페이지로 이동합니다.**

<p align="center">
    <a href="https://www.notion.so/Demo-Fresh-Trash-3cd71413eefe4bc385d13e7b2ea59bd4?pvs=4">
      <img width="130" alt="fresh-trash-logo" src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/c59a7b43-f772-4898-ab0d-829c358570cf">
    </a>
</p>
<p align="center">
  <img src="https://img.shields.io/badge/spring_boot-v2.7.18-green?logo=springboot"  alt="spring-boot" />
  <img src="https://img.shields.io/badge/mariadb-v10.11.7-blue?logo=mariadb" alt="mariadb"/>
  <img src="https://img.shields.io/badge/redis-v7.2.4-red?logo=redis" alt="redis"/>
</p>

**Fresh Trash는 중고 상품을 판매, 구매 할 수 있는 온라인 플랫폼입니다.**
- Fresh Trash에 가입해서 집안에 안쓰고 자리만 차지했던 중고상품을 **등록**하고 **판매, 나눔**하세요.
- 다른 유저가 등록한 중고상품를 살펴보고 **구매**해 보세요.
- 마음에 드는 중고상품를 **찜**하세요.
- 중고상품에 대해 궁금한 점이 있으면 **실시간 채팅**으로 물어보세요.

## Project Architecture

<p>
   <img src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/fad841ba-68bf-4567-85eb-401cf138532f" alt="architecture" />
</p>


## Tech Stacks

### Spring Framework

<p>
  <img src="https://user-images.githubusercontent.com/52682603/138834253-9bcd8b12-241f-41b2-85c4-d723a16bdb58.png" alt="spring_boot" width=15%>
  <img src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/4a5d84ba-d12d-48a5-aec0-36821aca646e" alt="Spring Security" width=13%>
  <img src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/9c872305-6245-4c10-b71f-925fee6dd83a" alt="RabbitMQ" width=15%>
  <img src="https://user-images.githubusercontent.com/52682603/138834280-73acd37b-97ef-4136-b58e-6138eb4fcc46.png" alt="query_dsl" width=15%>
  <img src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/e8d1d412-1039-4c39-9e41-12742a9bd080" alt="websocket" width=15%>
</p>

- **Spring Boot** 로 애플리케이션 서버를 구축했습니다.
- **Spring Data JPA(Hibernate)** 로 객체 지향 데이터 로직을 작성했습니다.
- **Spring Security** 를 통해 권한 관리를 하고, **JWT** 인증방식을 사용했습니다.
- **OAuth2** 를 사용하여 구글, 네이버, 카카오를 통한 간편 로그인합니다.
- **Spring Mail(+ Email Validation API)** 를 사용하여 이메일을 전송 및 유효성 검사를 합니다.
- **QueryDSL** 로 컴파일 시점에 SQL 오류를 감지합니다. 더 가독성 높은 코드를 작성할 수 있습니다. 
- **Spring WebSocket(+ STOMP)** 로 유저간에 채팅을 할 수 있습니다.
- **Spring AMQP(RabbitMQ)** 로 서버간의 의존성을 제거하고 고가용성을 보장합니다.
- **SSE(Server-Sent Event)** 를 사용해서 서버에서 클라이언트로 알림을 전송합니다.


### Database
<p>
   <img width=15% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/a25f6bf9-3ee0-490b-a056-177f2d2674ef" alt="mariadb" />
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/7b72cc13-95d0-453b-b79b-fd5b010f80cd" alt="redis" />
</p>

- 데이터베이스는 **MariaDB**를 사용합니다.
- 캐싱을 위해 NoSQL 데이터베이스인 **Redis**를 사용합니다.


### AWS
<p>
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/c0997875-9fb7-493d-a7d9-c089e011a436" alt="ec2" />
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/ea9f30a4-b460-4d6c-a71b-eb4f1e32ecf1" alt="s3" />
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/b0431ba3-aa03-4ffa-a2c6-d9d6c3c3b949" alt="s3" />
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/a446cf8a-2d93-45f1-a8d3-fdc6a8d7f518" alt="s3" />
</p>

- **Amazon EC2** 로 서버를 구축했습니다.
- **Amazon S3** 를 사용하여 이미지를 저장, 관리합니다.
- **Amazon RDS** 에서 데이터베이스를 관리합니다.
- **Amazon ElastiCache** 로 메모리 데이터베이스 캐싱을 관리합니다.

### CI / CD
<p>
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/9c02bfb1-caf0-44c1-aa74-355a2c0e66e7" alt="github_action" />
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/2fa86757-9c7a-4ab1-90ce-8580bd3a5d9e" alt="code_deploy" />
</p>

- **GithubAction, CodeDeploy** 으로 빌드, 테스트 및 지속적 배포를 진행합니다.

### Network
<p>
   <img width=15% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/6f41d019-15e0-419b-adc9-c8ccda0dc82e" alt="nginx" />
</p>

- Nginx를 리버스 프록시로 활용하고 있습니다.


## ERD

<a href="https://www.erdcloud.com/p/LqYiHP7d8rbqdWR5E">
   <img src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/9e1478e4-e52d-4a04-9044-20e3a3d5782c" alt="ERD" />
</a>


## 주요 기능

- [X] 로그인
- [X] 회원가입
- [X] 사용자 정보
- [X] 중고상품 조회/등록/수정/삭제
- [X] 알림 기능
- [X] 1:1 채팅 기능 
- [ ] 경매 조회/등록/삭제
- [ ] 경매 입찰/낙찰


