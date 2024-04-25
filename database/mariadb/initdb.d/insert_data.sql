insert into members(email, password, rating, nickname, address, file_name, login_type, user_role, account_status,
                    created_at, modified_at)
values ('abc@never.com', '$2a$10$ZskRs64yZZU9g6blmNdZte7FO6KaMduwlZhk7YkxJX2ZwYLSrPfA.', 10, 'abc',
        json_object('zipcode', '12345', 'state', 'state', 'city', 'city', 'district', 'district', 'detail', 'detail'),
        'test.png', 'GOOGLE', 'USER', 'ACTIVE', now(), null),
       ('def@never.com', '$2a$10$6UF8CDeP2A7ACjMVE/cpS.DlgRw/ZRA0NuHFnpziEpbasOhS6EGq.', 0, 'def',
        null, null, 'EMAIL', 'USER', 'ACTIVE', now(), null),
       ('user123@never.com', '$2a$10$6UF8CDeP2A7ACjMVE/cpS.DlgRw/ZRA0NuHFnpziEpbasOhS6EGq.', 0, 'user123',
        null, null, 'EMAIL', 'USER', 'ACTIVE', now(), null),
       ('user456@never.com', '$2a$10$6UF8CDeP2A7ACjMVE/cpS.DlgRw/ZRA0NuHFnpziEpbasOhS6EGq.', 0, 'user456',
        null, null, 'EMAIL', 'USER', 'ACTIVE', now(), null);


insert into wastes(member_id, title, content, waste_price, like_count, view_count, file_name, waste_category,
                   waste_status,
                   sell_status, address, created_at, modified_at, transaction_at)
values (1, 'title', 'content', 0, 1, 0, 'test.png', 'CLOTHING', 'GOOD', 'ONGOING',
                           json_object('zipcode', '12345', 'state', 'state', 'city', 'city', 'district', 'district', 'detail', 'detail'),
                           now(), null, null),
       (2, '보물단지1', '폐기물 설명 내용', 1000, 2, 3, '보물단지이미지.png', 'HEALTH', 'GOOD', 'ONGOING',
        json_object('zipcode', '16255', 'state', '경기도', 'city', '수원시', 'district', '팔달구', 'detail', '창룡대로'),
        now(), null, null),
       (3, '물건1', '물건 설명 내용', 0, 3, 4, '물건1.png', 'CLOTHING', 'GOOD', 'ONGOING',
        json_object('zipcode', '16666', 'state', '경기도', 'city', '수원시', 'district', '팔달구', 'detail', '창룡대로'),
        now(), null, null),
       (4, '물건2', '물건 설명 내용2', 0, 10, 5, '물건2.png', 'HEALTH', 'GOOD', 'ONGOING',
        json_object('zipcode', '16666', 'state', '경기도', 'city', '수원시', 'district', '팔달구', 'detail', '창룡대로'),
        now(), null, null),
       (1, 'title2', 'content2', 500, 1, 110, 'test2.png', 'FURNITURE_DECOR', 'BEST', 'CLOSE',
        json_object('zipcode', '12345', 'state', 'state', 'city', 'city', 'district', 'district', 'detail', 'detail'),
        now(), null, null),
       (2, '보물단지11', '폐기물 설명 내용11', 1000, 2, 3, '보물단지이미지11.png', 'FURNITURE_DECOR', 'GOOD', 'CLOSE',
        json_object('zipcode', '16255', 'state', '경기도', 'city', '수원시', 'district', '팔달구', 'detail', '창룡대로'),
        now(), null, null),
       (3, '물건12', '물건 설명 내용12', 0, 334, 433, '물건12.png', 'SPORTS', 'BEST', 'CLOSE',
        json_object('zipcode', '16666', 'state', '경기도', 'city', '수원시', 'district', '팔달구', 'detail', '창룡대로'),
        now(), null, null),
       (4, '물건23', '물건 설명 내용23', 0, 110, 51, '물건23.png', 'CLOTHING', 'GOOD', 'CLOSE',
        json_object('zipcode', '16666', 'state', '경기도', 'city', '수원시', 'district', '팔달구', 'detail', '창룡대로'),
        now(), null, null);


insert into waste_likes(member_id, waste_id, created_at)
values (2, 1, now()),
       (2, 2, now()),
       (2, 3, now()),
       (2, 4, now()),
       (2, 5, now()),
       (2, 6, now()),
       (2, 7, now()),
       (2, 8, now());

INSERT INTO chat_rooms(waste_id, seller_id, buyer_id, sell_status, open_or_close, created_at)
VALUES (1, 1, 2, 'ONGOING', 1, now()),
       (1, 1, 3, 'CLOSE', 1, now()),
       (1, 1, 4, 'ONGOING', 1, now());


INSERT INTO chat_messages(chat_room_id, member_id, message, created_at)
VALUES (1, 1, '첫 번째 메세지입니다.', now());

insert into transaction_logs(waste_id, seller_id, buyer_id, created_at)
values (1, 1, 2, now());

insert into alarms(member_id, alarm_type, alarm_args, message, created_at, read_at)
values (1, 'CHAT', json_object('fromMemberId', '2', 'targetId', '1'), '거래 완료되었습니다.', now(), null),
       (1, 'CHAT', json_object('fromMemberId', '1', 'targetId', '2'), '알람 테스트', now(), null),
       (1, 'TRANSACTION', json_object('fromMemberId', '3', 'targetId', '1'), '예약 요청이 왔습니다. 수락 또는 거절 해주세요', now(), null),
       (3, 'TRANSACTION', json_object('fromMemberId', '1', 'targetId', '1'), '예약이 거절되었습니다.', now(), null),
       (2, 'TRANSACTION', json_object('fromMemberId', '3', 'targetId', '1'), '판매 완료되었습니다.', now(), null),
       (3, 'TRANSACTION', json_object('fromMemberId', '2', 'targetId', '1'), '판매 완료되었습니다.', now(), null);

