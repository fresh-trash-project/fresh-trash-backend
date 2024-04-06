CREATE TABLE `members`
(
    `id`             bigint AUTO_INCREMENT NOT NULL,
    `email`          varchar(255)          NOT NULL,
    `password`       varchar(255)          NOT NULL,
    `rating`         double                NOT NULL,
    `nickname`       varchar(255)          NOT NULL,
    `address`        json,
    `file_name`      varchar(255),
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

CREATE TABLE `wastes`
(
    `id`             bigint AUTO_INCREMENT NOT NULL,
    `member_id`      bigint                NOT NULL,
    `title`          varchar(255)          NOT NULL,
    `content`        text                  NOT NULL,
    `waste_price`    integer               NOT NULL,
    `like_count`     integer               NOT NULL,
    `view_count`     integer               NOT NULL,
    `file_name`      varchar(255),
    `waste_category` varchar(255)          NOT NULL,
    `waste_status`   varchar(255)          NOT NULL,
    `sell_status`    varchar(255)          NOT NULL,
    `address`        json                  NOT NULL,
    `created_at`     datetime              NOT NULL,
    `modified_at`    datetime,
    `transaction_at` datetime,
    PRIMARY KEY (`id`),
    foreign key (`member_id`) references members (id) on delete cascade
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='폐기물';

CREATE TABLE `waste_likes`
(
    `id`         bigint AUTO_INCREMENT NOT NULL,
    `member_id`  bigint                NOT NULL,
    `waste_id`   bigint                NOT NULL,
    `created_at` datetime              NOT NULL,
    PRIMARY KEY (`id`),
    foreign key (`member_id`) references members (id) on delete cascade,
    foreign key (`waste_id`) references wastes (id) on delete cascade
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='관심 폐기물';
