insert into members(email, password, rating, nickname, address, file_name, login_type, user_role, account_status,
                    created_at, modified_at)
values ('abc@never.com', '$2a$10$4mbVj4n1KeBmNsQCeXZpZujVo.cmXMdPIoDUQ1c1jkR87LdBA4gJW', 10, 'abc',
        json_object('zipcode', '12345', 'state', 'state', 'city', 'city', 'district', 'district', 'detail', 'detail'),
        'test.png', 'GOOGLE', 'USER', 'ACTIVE', now(), null),
       ('def@never.com', '$2a$10$GdkQ8WABeKqQOXkMaildcePPAINn3IBs2V/As1c/S8Q9W1K7SfWsG', 0, 'def',
        null, null, 'EMAIL', 'USER', 'ACTIVE', now(), null);


insert into wastes(member_id, title, content, waste_price, like_count, view_count, file_name, waste_category,
                   waste_status,
                   sell_status, address, created_at, modified_at, transaction_at)
values (1, 'title', 'content', 0, 1, 0, 'test.png', 'CLOTHING', 'GOOD', 'ONGOING',
        json_object('zipcode', '12345', 'state', 'state', 'city', 'city', 'district', 'district', 'detail', 'detail'),
        now(), null, null),
       (2, '보물단지1', '폐기물 설명 내용', 0, 0, 0, '보물단지이미지.png', 'HEALTH', 'GOOD', 'ONGOING',
        json_object('zipcode', '16255', 'state', '경기도', 'city', '수원시', 'district', '팔달구', 'detail', '창룡대로'),
        now(), null, null);


insert into waste_likes(member_id, waste_id, created_at)
values (2, 1, now());


INSERT INTO chat_rooms(waste_id, seller_id, buyer_id, sell_status, open_or_close, created_at)
VALUES (1, 1, 2, 'ONGOING', 1, now());


INSERT INTO chat_messages(chat_room_id, member_id, message, created_at)
VALUES (1, 1, '첫 번째 메세지입니다.', now());

insert into transaction_logs(price, waste_id, seller_id, buyer_id, created_at) values
(1000, 1, 1, 2, now());

insert into alarms(member_id, alarm_type, alarm_args, created_at, read_at)
values (1, 'CHAT', json_object('fromMemberId', '2', 'targetId', '1'), now(), null);
