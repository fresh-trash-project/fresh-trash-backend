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
  <a href="https://codecov.io/gh/fresh-trash-project/fresh-trash-backend">
    <img src="https://codecov.io/gh/fresh-trash-project/fresh-trash-backend/graph/badge.svg?token=S3H22R68N2"/> 
  </a>
</p>

**Fresh Trash는 중고 상품을 판매, 구매, 경매 할 수 있는 온라인 플랫폼입니다.**

- Fresh Trash에 가입해서 집안에 안쓰고 자리만 차지했던 중고상품을 **등록**하고 **판매, 나눔**하세요.
- 판매하고 싶은 상품의 가치를 잘 모르겠다면 **경매**를 등록해보세요.
- 다른 유저가 등록한 중고상품를 살펴보고 **구매**해 보세요.
- 마음에 드는 중고상품를 **찜**하세요.
- 중고상품에 대해 궁금한 점이 있으면 **실시간 채팅**으로 물어보세요.

## Project Architecture

<p>
   <img src="https://github.com/user-attachments/assets/0fa9e9d8-5e96-45a6-99ff-188a2cb29430" alt="architecture" />
</p>

## 3-Layered Architecture

<p>
   <img src="https://github.com/user-attachments/assets/235d3586-2b35-48b9-a18f-0f57926d340c" alt="architecture" />
</p>

## 추천 시스템

- [fresh-trash-recsys](https://github.com/fresh-trash-project/fresh-trash-recsys)에 Python과 FastAPI를 활용하여 추천 시스템 서버를 구축했습니다.

## Tech Stacks

### Backend

<p>
  <img align="middle" src="https://user-images.githubusercontent.com/52682603/138834253-9bcd8b12-241f-41b2-85c4-d723a16bdb58.png" alt="spring_boot" width=15%>
  <img align="middle" src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/4a5d84ba-d12d-48a5-aec0-36821aca646e" alt="Spring Security" width=13%>
  <img align="middle" src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/9c872305-6245-4c10-b71f-925fee6dd83a" alt="RabbitMQ" width=15%>
  <img align="middle" src="https://user-images.githubusercontent.com/52682603/138834280-73acd37b-97ef-4136-b58e-6138eb4fcc46.png" alt="query_dsl" width=15%>
  <img align="middle" src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/e8d1d412-1039-4c39-9e41-12742a9bd080" alt="websocket" width=15%>
  <img align="middle" width=15% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/6f41d019-15e0-419b-adc9-c8ccda0dc82e" alt="nginx" />
</p>

- **Spring Boot** 로 애플리케이션 서버를 구축했습니다.
- **Spring Data JPA(Hibernate)** 로 객체 지향 데이터 로직을 작성했습니다.
- **Spring Security** 로 **JWT** 기반의 로그인/회원가입 기능을 구현했습니다.
- **OAuth2** 를 사용하여 구글, 네이버, 카카오를 통한 간편 로그인합니다.
- **Spring Mail(+ Email Validation API)** 를 사용하여 이메일을 전송 및 유효성 검사를 합니다.
- **QueryDSL** 로 컴파일 시점에 SQL 오류를 감지합니다. 더 가독성 높은 코드를 작성할 수 있습니다.
- **Spring WebSocket(+ STOMP)** 로 유저간에 채팅을 할 수 있습니다.
- **Spring AMQP(RabbitMQ)** 로 서버간의 의존성을 제거하고 고가용성을 보장합니다.
- **SSE(Server-Sent Event)** 를 사용해서 서버에서 클라이언트로 알림을 전송합니다.
- **Nginx**를 리버스 프록시로 활용하여 CORS 에러를 해결하고 HTTPS를 적용하여 보안을 강화했습니다. 

### Database

<p>
   <img width=15% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/a25f6bf9-3ee0-490b-a056-177f2d2674ef" alt="mariadb" />
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/7b72cc13-95d0-453b-b79b-fd5b010f80cd" alt="redis" />
</p>

- 데이터베이스는 **MariaDB**를 사용합니다.
- 캐싱을 위해 NoSQL 데이터베이스인 **Redis**를 사용합니다.

### Data

<p>
    <img width=15% src="https://github.com/user-attachments/assets/ea89d563-4123-4074-af55-723f85e4632e" alt="grafana" />
    <img width=15% src="https://github.com/user-attachments/assets/c3b9d6c1-9c90-4af7-ac19-e4f622c8d839" alt="prometheus" />
</p>

- **Grafana** 를 통해 성능 차트, 그래프 등을 시각화하여 모니터링합니다.
- **Prometheus** 를 통해 모니터링을 위한 메트릭(metric)을 저장합니다.

### AWS

<p>
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/c0997875-9fb7-493d-a7d9-c089e011a436" alt="ec2" />
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/ea9f30a4-b460-4d6c-a71b-eb4f1e32ecf1" alt="s3" />
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/b0431ba3-aa03-4ffa-a2c6-d9d6c3c3b949" alt="s3" />
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/a446cf8a-2d93-45f1-a8d3-fdc6a8d7f518" alt="s3" />
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/2fa86757-9c7a-4ab1-90ce-8580bd3a5d9e" alt="code_deploy" />
</p>

- **Amazon EC2** 로 서버를 구축했습니다.
- **Amazon S3** 를 사용하여 이미지를 저장, 관리합니다.
- **Amazon RDS** 에서 데이터베이스를 관리합니다.
- **Amazon ElastiCache** 로 메모리 데이터베이스 캐싱을 관리합니다.
- **CodeDeploy** 로 지속적 배포(CI)를 진행합니다.

### DevOps

<p>
   <img width=13% src="https://github.com/fresh-trash-project/fresh-trash-backend/assets/82129206/9c02bfb1-caf0-44c1-aa74-355a2c0e66e7" alt="github_action" />
   <img width=13% src="https://github.com/user-attachments/assets/d6a69903-1069-4d7f-bb18-20ade101927f" alt="docker" />
</p>

- **GithubAction** 으로 자동 배포, 코드 커버리지 측정, 코드 리뷰를 진행합니다.
- **Docker** 로 일관성있는 개발 환경을 구축합니다.

### Testing Tools

<p>
   <img align="middle" width=13% src="https://github.com/user-attachments/assets/57cc13f2-f06d-4ba8-a2a0-628145472314" alt="mockito" />
    <img align="middle" width=13% src="https://github.com/user-attachments/assets/98c7d351-32c2-4882-83ba-f9e188bcf5da" alt="jacoco" />
    <img align="middle" width=15% src="https://github.com/user-attachments/assets/b5050f3a-6815-4a10-b836-5b71c7c890c5" alt="codecov" />
    <img align="middle" width=15% src="https://github.com/user-attachments/assets/9f32eefb-2f15-48e8-8cd4-b04ae8d0332f" alt="jmeter" />
</p>

- **Mockito** 를 활용하여 단위(Unit) 테스트를 수행합니다.
- **JaCoCo** 로 코드 커버리지를 측정하고, **CodeCov** 에 Report 를 업로드합니다.
- **Jmeter** 로 부하 테스트, 동시성 테스트를 수행합니다.

## ERD

<a href="https://www.erdcloud.com/p/t6LvnnESYwAQtyfGX">
   <img src="https://github.com/user-attachments/assets/2d83bbfd-3257-4d29-bdc2-5c5bb3586de2" alt="ERD" />
</a>

## 주요 기능

- [X] 로그인
- [X] 회원가입
- [X] 사용자 정보
- [X] 중고상품 조회/등록/수정/삭제
- [X] 알림 기능
- [X] 1:1 채팅 기능
- [X] 경매 조회/등록/삭제
- [X] 경매 입찰/낙찰


