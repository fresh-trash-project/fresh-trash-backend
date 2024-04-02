CREATE TABLE `wastes`
(
    `id`             bigint AUTO_INCREMENT NOT NULL,
    `title`          varchar(255)          NOT NULL,
    `content`        text                  NOT NULL,
    `waste_price`     integer               NOT NULL,
    `like_count`      integer               NOT NULL,
    `view_count`      integer               NOT NULL,
    `file_name`       varchar(255)          NULL,
    `waste_category`  varchar(255)          NOT NULL,
    `waste_status`    varchar(255)          NOT NULL,
    `sell_status`     varchar(255)          NOT NULL,
    `address`        json                  NOT NULL,
    `created_at`     datetime              NOT NULL,
    `modified_at`    datetime,
    `transaction_at` datetime,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='폐기물';