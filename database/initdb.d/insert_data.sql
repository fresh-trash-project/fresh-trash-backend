insert into members(email, password, rating, nickname, address, file_name, login_type, user_role, account_status,
                    created_at, modified_at)
values ('abc@never.com', '$2a$10$4mbVj4n1KeBmNsQCeXZpZujVo.cmXMdPIoDUQ1c1jkR87LdBA4gJW', 10, 'abc',
        json_object('zipcode', '12345', 'state', 'state', 'city', 'city', 'district', 'district', 'detail', 'detail'),
        'test.png', 'GOOGLE', 'USER', 'ACTIVE', now(), null);


insert into wastes(member_id, title, content, waste_price, like_count, view_count, file_name, waste_category, waste_status,
                   sell_status, address, created_at, modified_at, transaction_at)
values (1, 'title', 'content', 0, 0, 0, 'test.png', 'CLOTHING', 'GOOD', 'ONGOING',
        json_object('zipcode', '12345', 'state', 'state', 'city', 'city', 'district', 'district', 'detail', 'detail'),
        now(), null, null);