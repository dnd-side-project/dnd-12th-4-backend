create TABLE if not exists `pickitalki`.members
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    kakao_id          BIGINT UNIQUE NOT NULL,
    email             VARCHAR(100) UNIQUE,
    nick_name         VARCHAR(50)   NULL,
    profile_image_url TEXT          NULL,
    refresh_token     TEXT          NULL,
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME      DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    is_deleted        TINYINT(1)    NOT NULL DEFAULT 0
);

create TABLE if not exists `pickitalki`.channels
(
    uuid       BINARY(16) PRIMARY KEY,
    name       VARCHAR(30) NOT NULL UNIQUE,
    invite_code VARCHAR(6) NOT NULL UNIQUE,
    point INT NOT NULL DEFAULT 0,
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME    DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    is_deleted TINYINT(1)  NOT NULL DEFAULT 0
);

create TABLE if not exists `pickitalki`.channel_members
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_uuid     BINARY(16)  NOT NULL,
    member_id        BIGINT      NOT NULL,
    member_code_name VARCHAR(20),
    profile_image TEXT NULL,
    is_using_default_profile TINYINT(1) NOT NULL DEFAULT 1,
    role             VARCHAR(10) NOT NULL,
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME    DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    is_deleted       TINYINT(1)  NOT NULL DEFAULT 0,
    FOREIGN KEY (channel_uuid) REFERENCES channels (uuid),
    FOREIGN KEY (member_id) REFERENCES members (id)
);

create TABLE if not exists `pickitalki`.questions
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_uuid   BINARY(16)   NOT NULL,
    channel_member_id      BIGINT       NOT NULL,
    content        VARCHAR(255) NOT NULL,
    question_number BIGINT NOT NULL,
    is_anonymous   BOOLEAN      NOT NULL DEFAULT FALSE,
    anonymous_name VARCHAR(30),
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_date   DATE     NOT NULL,
    updated_at     DATETIME     DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    is_deleted     TINYINT(1)   NOT NULL DEFAULT 0,
    CONSTRAINT unique_channel_today_question UNIQUE (channel_uuid, created_date),
    FOREIGN KEY (channel_uuid) REFERENCES channels (uuid),
    FOREIGN KEY (channel_member_id) REFERENCES channel_members (id)

);

create TABLE if not exists `pickitalki`.answers
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id    BIGINT       NOT NULL,
    member_id      BIGINT       NOT NULL,
    content        VARCHAR(500) NOT NULL,
    is_anonymous   BOOLEAN      NOT NULL DEFAULT FALSE,
    anonymous_name VARCHAR(10),
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    is_deleted     TINYINT(1)   NOT NULL DEFAULT 0,
    FOREIGN KEY (question_id) REFERENCES questions (id),
    FOREIGN KEY (member_id) REFERENCES members (id)
);

