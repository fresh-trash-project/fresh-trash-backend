insert into members(email, password, rating, nickname, address, file_name, login_type, user_role, account_status,
                    created_at, modified_at, flag_count)
values ('abc@never.com', '$2a$10$ZskRs64yZZU9g6blmNdZte7FO6KaMduwlZhk7YkxJX2ZwYLSrPfA.', 5, 'abc',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        'test.png', 'EMAIL', 'USER', 'ACTIVE', now(), null, 0),
       ('def@never.com', '$2a$10$6UF8CDeP2A7ACjMVE/cpS.DlgRw/ZRA0NuHFnpziEpbasOhS6EGq.', 4, 'def',
        null, null, 'EMAIL', 'USER', 'ACTIVE', now(), null, 0),
       ('user123@never.com', '$2a$10$6UF8CDeP2A7ACjMVE/cpS.DlgRw/ZRA0NuHFnpziEpbasOhS6EGq.', 3, 'user123',
        null, null, 'EMAIL', 'USER', 'ACTIVE', now(), null, 0),
       ('user456@never.com', '$2a$10$6UF8CDeP2A7ACjMVE/cpS.DlgRw/ZRA0NuHFnpziEpbasOhS6EGq.', 2, 'user456',
        null, null, 'EMAIL', 'USER', 'ACTIVE', now(), null, 0),
       ('user777@never.com', '$2a$10$6UF8CDeP2A7ACjMVE/cpS.DlgRw/ZRA0NuHFnpziEpbasOhS6EGq.', 2, 'user777',
        null, null, 'EMAIL', 'USER', 'ACTIVE', now(), null, 0),
       ('blackUser@gmail.com', '$2a$10$6UF8CDeP2A7ACjMVE/cpS.DlgRw/ZRA0NuHFnpziEpbasOhS6EGq.', 2, 'blackUser',
        null, null, 'EMAIL', 'BLACK_USER', 'ACTIVE', now(), null, 10);


insert into products(member_id, title, content, product_price, like_count, view_count, file_name, product_category,
                     product_status,
                     sell_status, address, created_at, modified_at, product_deal_at)
values (1, 'title', 'content', 0, 1, 0, 'test.png', 'CLOTHING', 'GOOD', 'ONGOING',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (2, '보물단지1', '폐기물 설명 내용', 1000, 2, 3, '보물단지이미지.png', 'HEALTH', 'GOOD', 'ONGOING',
        json_object('zipcode': '16255', 'state': '경기도', 'city': '수원시', 'district': '팔달구', 'detail': '창룡대로'),
        now(), null, null),
       (3, '물건1', '물건 설명 내용', 0, 3, 4, '물건1.png', 'CLOTHING', 'GOOD', 'ONGOING',
        json_object('zipcode': '16666', 'state': '경기도', 'city': '수원시', 'district': '팔달구', 'detail': '창룡대로'),
        now(), null, null),
       (4, '물건2', '물건 설명 내용2', 0, 10, 5, '물건2.png', 'HEALTH', 'GOOD', 'ONGOING',
        json_object('zipcode': '16666', 'state': '경기도', 'city': '수원시', 'district': '팔달구', 'detail': '창룡대로'),
        now(), null, null),
       (1, 'title2', 'content2', 500, 1, 110, 'test2.png', 'FURNITURE_DECOR', 'BEST', 'CLOSE',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (2, '보물단지11', '폐기물 설명 내용11', 1000, 2, 3, '보물단지이미지11.png', 'FURNITURE_DECOR', 'GOOD', 'CLOSE',
        json_object('zipcode': '16255', 'state': '경기도', 'city': '수원시', 'district': '팔달구', 'detail': '창룡대로'),
        now(), null, null),
       (3, '물건12', '물건 설명 내용12', 0, 334, 433, '물건12.png', 'SPORTS', 'BEST', 'CLOSE',
        json_object('zipcode': '16666', 'state': '경기도', 'city': '수원시', 'district': '팔달구', 'detail': '창룡대로'),
        now(), null, null),
       (4, '물건23', '물건 설명 내용23', 0, 110, 51, '물건23.png', 'CLOTHING', 'GOOD', 'CLOSE',
        json_object('zipcode': '16666', 'state': '경기도', 'city': '수원시', 'district': '팔달구', 'detail': '창룡대로'),
        now(), null, null),
       (2, '보물단지22', '폐기물 설명 내용11', 1000, 2, 3, '보물단지이미지22.png', 'FURNITURE_DECOR', 'GOOD', 'CLOSE',
        json_object('zipcode': '16255', 'state': '경기도', 'city': '수원시', 'district': '팔달구', 'detail': '창룡대로'),
        now(), null, null),
       (3, '물건123', '물건 설명 내용12', 0, 334, 433, '물건123.png', 'SPORTS', 'BEST', 'CLOSE',
        json_object('zipcode': '16666', 'state': '경기도', 'city': '수원시', 'district': '팔달구', 'detail': '창룡대로'),
        now(), null, null),
       (4, '물건234', '물건 설명 내용23', 0, 110, 51, '물건234.png', 'CLOTHING', 'GOOD', 'CLOSE',
        json_object('zipcode': '16666', 'state': '경기도', 'city': '수원시', 'district': '팔달구', 'detail': '창룡대로'),
        now(), null, null),
       (2, '보물단지33', '폐기물 설명 내용11', 1000, 2, 3, '보물단지이미지33.png', 'FURNITURE_DECOR', 'GOOD', 'CLOSE',
        json_object('zipcode': '16255', 'state': '경기도', 'city': '수원시', 'district': '팔달구', 'detail': '창룡대로'),
        now(), null, null),
       (3, '물건2', '물건 설명 내용12', 0, 334, 433, '물건2.png', 'SPORTS', 'BEST', 'CLOSE',
        json_object('zipcode': '16666', 'state': '경기도', 'city': '수원시', 'district': '팔달구', 'detail': '창룡대로'),
        now(), null, null),
       (1, 'title2', 'content2', 500, 1, 110, 'test2.png', 'FURNITURE_DECOR', 'WORST', 'CLOSE',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (1, 'title3', 'content3', 0, 23, 234, 'test3.png', 'FURNITURE_DECOR', 'GOOD', 'CLOSE',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (1, 'title4', 'content4', 2500, 578, 345, 'test4.png', 'FURNITURE_DECOR', 'GOOD', 'CLOSE',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (1, 'title5', 'content5', 3000, 2345, 34, 'test5.png', 'FURNITURE_DECOR', 'NORMAL', 'CLOSE',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (1, 'title6', 'content6', 0, 34, 56, 'test6.png', 'FURNITURE_DECOR', 'NORMAL', 'CLOSE',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (1, 'title7', 'content7', 500, 456, 90, 'test7.png', 'FURNITURE_DECOR', 'WORST', 'CLOSE',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (1, 'title8', 'content', 500, 456, 90, 'test8.png', 'FURNITURE_DECOR', 'BEST', 'CLOSE',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (1, 'title9', 'content', 500, 43, 90, 'test9.png', 'FURNITURE_DECOR', 'BEST', 'ONGOING',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (1, 'title10', 'content', 500, 456, 789, 'test10.png', 'FURNITURE_DECOR', 'GOOD', 'ONGOING',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (1, 'title11', 'content', 500, 23, 90, 'test11.png', 'FURNITURE_DECOR', 'GOOD', 'ONGOING',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (1, 'title12', 'content', 0, 456, 56, 'test12.png', 'FURNITURE_DECOR', 'NORMAL', 'ONGOING',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (1, 'title13', 'content', 500, 234, 90, 'test13.png', 'FURNITURE_DECOR', 'BEST', 'ONGOING',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (1, 'title14', 'content', 0, 456, 456, 'test14.png', 'FURNITURE_DECOR', 'WORST', 'ONGOING',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null),
       (1, 'title15', 'content', 500, 456, 90, 'test15.png', 'FURNITURE_DECOR', 'NORMAL', 'ONGOING',
        json_object('zipcode': '12345', 'state': 'state', 'city': 'city', 'district': 'district', 'detail': 'detail'),
        now(), null, null);


insert into product_likes(member_id, product_id, created_at)
values (1, 1, now()),
       (1, 2, now()),
       (1, 3, now()),
       (1, 4, now()),
       (1, 5, now()),
       (1, 6, now()),
       (1, 7, now()),
       (1, 8, now()),
       (2, 4, now()),
       (3, 5, now()),
       (4, 6, now()),
       (2, 7, now()),
       (3, 8, now());

INSERT INTO chat_rooms(product_id, seller_id, buyer_id, sell_status, open_or_close, created_at)
VALUES (1, 1, 2, 'ONGOING', 1, now()),
       (1, 1, 3, 'CLOSE', 1, now()),
       (1, 1, 4, 'ONGOING', 1, now());


INSERT INTO chat_messages(chat_room_id, member_id, message, created_at)
VALUES (1, 1, '첫 번째 메세지입니다.', now());

insert into product_deal_logs(product_id, seller_id, buyer_id, created_at)
values (5, 1, 2, now()),
       (6, 2, 1, now()),
       (7, 3, 1, now()),
       (8, 4, 1, now()),
       (9, 2, 1, now()),
       (10, 3, 1, now()),
       (11, 4, 1, now()),
       (12, 2, 1, now()),
       (13, 3, 1, now()),
       (14, 1, 2, now()),
       (15, 1, 2, now()),
       (16, 1, 2, now()),
       (17, 1, 2, now()),
       (18, 1, 2, now()),
       (19, 1, 2, now()),
       (20, 1, 2, now());

insert into alarms(member_id, alarm_type, alarm_args, message, created_at, read_at, deleted_at)
values (1, 'CHAT', json_object('fromMemberId': 2, 'targetId': 1), '거래 완료되었습니다.', now(), null, null),
       (1, 'CHAT', json_object('fromMemberId': 1, 'targetId': 2), '알람 테스트', now(), null, null),
       (1, 'TRANSACTION', json_object('fromMemberId': 3, 'targetId': 1), 'user123님이 예약중으로 변경하였습니다.', now(), null, null),
       (1, 'TRANSACTION', json_object('fromMemberId': 3, 'targetId': 1), 'user123님이 판매중으로 변경하였습니다.', now(), null, null),
       (1, 'TRANSACTION', json_object('fromMemberId': 3, 'targetId': 1), 'user123님이 예약중으로 변경하였습니다.', now(), null, null),
       (1, 'TRANSACTION', json_object('fromMemberId': 3, 'targetId': 1), 'user123님이 판매중으로 변경하였습니다.', now(), null, null),
       (1, 'CHAT', json_object('fromMemberId': 2, 'targetId': 5), '거래 완료되었습니다.', now(), now(), null),
       (1, 'CHAT', json_object('fromMemberId': 1, 'targetId': 6), '알람 테스트', now(), now(), null),
       (1, 'TRANSACTION', json_object('fromMemberId': 3, 'targetId': 5), 'user123님이 예약중으로 변경하였습니다.', now(), now(),
        null),
       (1, 'TRANSACTION', json_object('fromMemberId': 3, 'targetId': 5), 'user123님이 판매중으로 변경하였습니다.', now(), now(),
        null),
       (1, 'TRANSACTION', json_object('fromMemberId': 3, 'targetId': 5), 'user123님이 예약중으로 변경하였습니다.', now(), now(),
        null),
       (1, 'TRANSACTION', json_object('fromMemberId': 3, 'targetId': 5), 'user123님이 판매중으로 변경하였습니다.', now(), now(),
        null),
       (1, 'TRANSACTION', json_object('fromMemberId': 3, 'targetId': 1), '거래 완료되었습니다.', now(), null, null),
       (1, 'TRANSACTION', json_object('fromMemberId': 4, 'targetId': 4), '예약중으로 변경하였습니다.', now(), null, null),
       (1, 'TRANSACTION', json_object('fromMemberId': 4, 'targetId': 4), '거래 완료되었습니다.', now(), null, null),
       (1, 'FLAG', json_object('fromMemberId': 1, 'targetId': 1),
        '1번 신고받은 내역이 있습니다. 신고받은 횟수가 10번이상 되면 서비스를 이용하실 수 없습니다.', now(), null, null),
       (2, 'TRANSACTION', json_object('fromMemberId': 1, 'targetId': 1), 'abc님이 판매중으로 변경하였습니다.', now(), null, null),
       (2, 'TRANSACTION', json_object('fromMemberId': 1, 'targetId': 1), '판매 완료되었습니다.', now(), null, null),
       (2, 'TRANSACTION', json_object('fromMemberId': 1, 'targetId': 1), '판매 완료되었습니다.', now(), null, null),
       (2, 'FLAG', json_object('fromMemberId': 1, 'targetId': 1),
        '1번 신고받은 내역이 있습니다. 신고받은 횟수가 10번이상 되면 서비스를 이용하실 수 없습니다.', now(), null, null),
       (2, 'TRANSACTION', json_object('fromMemberId': 3, 'targetId': 2), 'user123님이 예약중으로 변경했습니다.', now(), null, null),
       (2, 'TRANSACTION', json_object('fromMemberId': 3, 'targetId': 2), '판매 완료되었습니다.', now(), null, null),
       (2, 'TRANSACTION', json_object('fromMemberId': 3, 'targetId': 1), '판매 완료되었습니다.', now(), null, null),
       (3, 'TRANSACTION', json_object('fromMemberId': 1, 'targetId': 1), 'abc님이 판매중으로 변경하였습니다.', now(), null, null),
       (3, 'TRANSACTION', json_object('fromMemberId': 1, 'targetId': 1), 'abc님이 예약중으로 변경하였습니다.', now(), null, null),
       (3, 'TRANSACTION', json_object('fromMemberId': 2, 'targetId': 1), '판매 완료되었습니다.', now(), null, null),
       (4, 'TRANSACTION', json_object('fromMemberId': 1, 'targetId': 1), 'abc님이 판매중으로 변경하였습니다.', now(), null, null),
       (5, 'TRANSACTION', json_object('fromMemberId': 1, 'targetId': 1), 'abc님이 판매중으로 변경하였습니다.', now(), null, null),
       (1, 'TRANSACTION', json_object('fromMemberId': 1, 'targetId': 1), 'abc님이 판매중으로 변경하였습니다.', now(), null, now());

insert into auctions(member_id, file_name, title, content, product_category, product_status, auction_status, final_bid,
                     view_count, started_at, ended_at, created_at, version)
values (1, 'test.png', 'title', 'content', 'CLOTHING', 'GOOD', 'ONGOING', 1000, 1, now(),
        dateadd(month, 1, now()), now(), 0),
       (2, 'test.png', 'title2', 'content2', 'HEALTH', 'GOOD', 'ONGOING', 1000, 3, now(),
        dateadd(month, 1, now()), now(), 0),
       (3, 'test.png', 'title3', 'content3', 'SPORTS', 'GOOD', 'CLOSE', 3000, 5, now(),
        dateadd(month, 1, now()), now(), 0),
       (4, 'test.png', 'title4', 'content4', 'SPORTS', 'GOOD', 'ONGOING', 3000, 5, now(), now(), now(), 0),
       (5, 'test.png', 'title5', 'content5', 'CLOTHING', 'BEST', 'ONGOING', 3000, 5, now(), now(), now(), 0);

insert into bidding_history(member_id, auction_id, price, is_pay, success_bid_at, created_at)
values (2, 1, 1000, true, now(), now()),
       (1, 2, 1000, false, null, now()),
       (1, 2, 2000, false, null, now()),
       (1, 3, 3000, false, now(), now());

insert into auction_reviews(member_id, auction_id, rating, content, created_at)
values (1, 2, 3, 'good', now()),
       (2, 3, 2, '.', now()),
       (3, 4, 1, '좋음', now()),
       (4, 1, 5, '', now());
