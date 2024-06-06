CREATE TABLE `members`
(
    `id`             bigint AUTO_INCREMENT NOT NULL,
    `email`          varchar(255)          NOT NULL,
    `password`       varchar(255)          NOT NULL,
    `rating`         double                NOT NULL,
    `nickname`       varchar(255)          NOT NULL,
    `address`        json,
    `file_name`      varchar(255),
    `flag_count`     integer DEFAULT 0     NOT NULL,
    `login_type`     varchar(255)          NOT NULL,
    `user_role`      varchar(255)          NOT NULL,
    `account_status` varchar(255)          NOT NULL,
    `created_at`     datetime              NOT NULL,
    `modified_at`    datetime,
    PRIMARY KEY (`id`),
    UNIQUE KEY (`email`),
    UNIQUE KEY (`nickname`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='멤버';


CREATE TABLE `products`
(
    `id`               bigint AUTO_INCREMENT NOT NULL,
    `member_id`        bigint                NOT NULL,
    `title`            varchar(255)          NOT NULL,
    `content`          text                  NOT NULL,
    `product_price`    integer               NOT NULL,
    `like_count`       integer               NOT NULL,
    `view_count`       integer               NOT NULL,
    `file_name`        varchar(255)          NOT NULL,
    `product_category` varchar(255)          NOT NULL,
    `product_status`   varchar(255)          NOT NULL,
    `sell_status`      varchar(255)          NOT NULL,
    `address`          json                  NOT NULL,
    `created_at`       datetime              NOT NULL,
    `modified_at`      datetime,
    `product_deal_at`  datetime,
    PRIMARY KEY (`id`),
    foreign key (`member_id`) references members (id) on delete cascade
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='폐기물';


CREATE TABLE `product_reviews`
(
    `id`         bigint AUTO_INCREMENT NOT NULL,
    `member_id`  bigint                NOT NULL,
    `product_id` bigint                NOT NULL,
    `rating`     integer               NOT NULL,
    `created_at` datetime              NOT NULL,
    PRIMARY KEY (`id`),
    foreign key (`member_id`) references members (id) on delete cascade,
    foreign key (`product_id`) references products (id) on delete cascade
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='폐기물 리뷰';


CREATE TABLE `product_likes`
(
    `id`         bigint AUTO_INCREMENT NOT NULL,
    `member_id`  bigint                NOT NULL,
    `product_id` bigint                NOT NULL,
    `created_at` datetime              NOT NULL,
    PRIMARY KEY (`id`),
    foreign key (`member_id`) references members (id) on delete cascade,
    foreign key (`product_id`) references products (id) on delete cascade
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='관심 폐기물';


CREATE UNIQUE INDEX member_id_and_product_id ON product_likes (member_id, product_id);

CREATE TABLE `chat_rooms`
(
    `id`            bigint AUTO_INCREMENT NOT NULL,
    `product_id`    bigint                NOT NULL,
    `seller_id`     bigint                NOT NULL,
    `buyer_id`      bigint                NOT NULL,
    `sell_status`   varchar(255)          NOT NULL,
    `open_or_close` tinyint(1)            NOT NULL,
    `created_at`    datetime              NOT NULL,
    PRIMARY KEY (`id`),
    foreign key (`product_id`) references `products` (`id`) on delete cascade,
    foreign key (`seller_id`) references `members` (`id`),
    foreign key (`buyer_id`) references `members` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='채팅방';


CREATE UNIQUE INDEX product_id_and_seller_id_and_buyer_id ON chat_rooms (product_id, seller_id, buyer_id);


CREATE TABLE `chat_messages`
(
    `id`           bigint AUTO_INCREMENT NOT NULL,
    `chat_room_id` bigint                NOT NULL,
    `member_id`    bigint                NOT NULL,
    `message`      TEXT                  NOT NULL,
    `created_at`   datetime              NOT NULL,
    PRIMARY KEY (`id`),
    foreign key (`chat_room_id`) references `chat_rooms` (`id`) on delete cascade,
    foreign key (`member_id`) references `members` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='채팅 메세지';

CREATE TABLE `product_deal_logs`
(
    `id`         bigint AUTO_INCREMENT NOT NULL,
    `product_id` bigint                NOT NULL,
    `seller_id`  bigint                NOT NULL,
    `buyer_id`   bigint                NOT NULL,
    `created_at` datetime              NOT NULL,
    PRIMARY KEY (`id`),
    foreign key (`product_id`) references `products` (`id`) on delete cascade,
    foreign key (`seller_id`) references `members` (`id`),
    foreign key (`buyer_id`) references `members` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='거래내역';

CREATE TABLE `alarms`
(
    `id`         bigint AUTO_INCREMENT NOT NULL,
    `member_id`  bigint                NOT NULL,
    `alarm_type` varchar(255)          NOT NULL,
    `alarm_args` json                  NOT NULL,
    `message`    varchar(255)          NOT NULL,
    `created_at` datetime              NOT NULL,
    `read_at`    datetime,
    `deleted_at` datetime,
    PRIMARY KEY (`id`),
    foreign key (`member_id`) references members (id) on delete cascade
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='알람';

CREATE TABLE `auctions`
(
    `id`               bigint AUTO_INCREMENT NOT NULL,
    `member_id`        bigint                NOT NULL,
    `file_name`        varchar(255)          NOT NULL,
    `title`            varchar(255)          NOT NULL,
    `content`          text                  NOT NULL,
    `product_category` varchar(255)          NOT NULL,
    `product_status`   varchar(255)          NOT NULL,
    `auction_status`   varchar(255)          NOT NULL,
    `final_bid`          integer               NOT NULL,
    `view_count`       integer               NOT NULL,
    `started_at`       datetime              NOT NULL,
    `ended_at`         datetime              NOT NULL,
    `created_at`       datetime              NOT NULL,
    `version`          integer default 0     NOT NULL,
    PRIMARY KEY (`id`),
    foreign key (`member_id`) references members (id) on delete cascade
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='경매';

CREATE TABLE `bidding_history`
(
    `id` bigint AUTO_INCREMENT NOT NULL ,
    `member_id` bigint NOT NULL ,
    `auction_id` bigint NOT NULL ,
    `price` integer NOT NULL ,
    `is_success_bidding` tinyint(1) NOT NULL ,
    `created_at` datetime NOT NULL ,
    PRIMARY KEY (`id`),
    foreign key (`member_id`) references members (id) on delete cascade,
    foreign key (`auction_id`) references auctions (id) on delete cascade
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='입찰 내역';