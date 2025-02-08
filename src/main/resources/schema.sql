CREATE TABLE IF NOT EXISTS members
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    kakao_id          BIGINT UNIQUE NOT NULL,
    email             VARCHAR(100) UNIQUE,
    nick_name         VARCHAR(50)   NOT NULL,
    profile_image_url TEXT          NULL,
    refresh_token     TEXT          NULL,
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted        TINYINT(1)    NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS channels
(
    uuid       BINARY(16) PRIMARY KEY,
    name       VARCHAR(30) NOT NULL,
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1)  NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS channel_members
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_uuid     BINARY(16)  NOT NULL,
    member_id        BIGINT      NOT NULL,
    member_code_name VARCHAR(20),
    role             VARCHAR(10) NOT NULL,
    created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted       TINYINT(1)  NOT NULL DEFAULT 0,
    FOREIGN KEY (channel_uuid) REFERENCES channels (uuid),
    FOREIGN KEY (member_id) REFERENCES members (id)
);

CREATE TABLE IF NOT EXISTS questions
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_uuid      BINARY(16)   NOT NULL,
    channel_member_id BIGINT       NOT NULL,
    content           VARCHAR(100) NOT NULL,
    question_number   BIGINT       NOT NULL,
    is_anonymous      BOOLEAN      NOT NULL DEFAULT FALSE,
    author_name       VARCHAR(10),
    created_date      DATE         NOT NULL,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted        TINYINT(1)   NOT NULL DEFAULT 0,
    FOREIGN KEY (channel_uuid) REFERENCES channels (uuid),
    FOREIGN KEY (channel_member_id) REFERENCES channel_members (id),
    CONSTRAINT unique_channel_today_question UNIQUE (channel_uuid, created_date)
);

CREATE TABLE IF NOT EXISTS answers
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id       BIGINT       NOT NULL,
    channel_member_id BIGINT       NOT NULL,
    content           VARCHAR(500) NOT NULL,
    is_anonymous      BOOLEAN      NOT NULL DEFAULT FALSE,
    anonymous_name    VARCHAR(30),
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted        TINYINT(1)   NOT NULL DEFAULT 0,
    FOREIGN KEY (question_id) REFERENCES questions (id),
    FOREIGN KEY (channel_member_id) REFERENCES channel_members (id)
);

CREATE TABLE IF NOT EXISTS tutorial
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT      NOT NULL,
    status     VARCHAR(10) NOT NULL,
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted TINYINT(1)  NOT NULL DEFAULT 0,
    FOREIGN KEY (member_id) REFERENCES members (id)
);
