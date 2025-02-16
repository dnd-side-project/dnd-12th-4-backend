create TABLE if not exists `pickitalki`.channels
(
    uuid        BINARY(16)                           NOT NULL PRIMARY KEY,
    name        VARCHAR(30)                          NOT NULL,
    invite_code VARCHAR(6)                           NOT NULL,
    point       INT        DEFAULT 0                 NOT NULL,
    created_at  DATETIME   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at  DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    is_deleted  TINYINT(1) DEFAULT 0                 NOT NULL,
    CONSTRAINT invite_code UNIQUE (invite_code),
    CONSTRAINT name UNIQUE (name)
);

create TABLE if not exists `pickitalki`.members
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    kakao_id          BIGINT                               NOT NULL,
    email             VARCHAR(100)                         NULL,
    nick_name         VARCHAR(50)                          NULL,
    profile_image_url TEXT                                 NULL,
    refresh_token     TEXT                                 NULL,
    created_at        DATETIME   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    is_deleted        TINYINT(1) DEFAULT 0                 NOT NULL,
    CONSTRAINT email UNIQUE (email),
    CONSTRAINT kakao_id UNIQUE (kakao_id)
);

create TABLE if not exists `pickitalki`.channel_members
(
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_uuid             BINARY(16)                           NOT NULL,
    member_id                BIGINT                               NOT NULL,
    member_code_name         VARCHAR(20)                          NULL,
    profile_image            TEXT                                 NULL,
    is_using_default_profile TINYINT(1) DEFAULT 1                 NOT NULL,
    role                     VARCHAR(10)                          NOT NULL,
    created_at               DATETIME   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at               DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    is_deleted               TINYINT(1) DEFAULT 0                 NOT NULL,
    CONSTRAINT channel_members_fk FOREIGN KEY (channel_uuid) REFERENCES channels (uuid) ON DELETE CASCADE,
    CONSTRAINT channel_members_ibfk_2 FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
);

create TABLE if not exists `pickitalki`.questions
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_uuid      BINARY(16)                           NOT NULL,
    channel_member_id BIGINT                               NULL,
    content           VARCHAR(255)                         NOT NULL,
    question_number   BIGINT                               NOT NULL,
    is_anonymous      TINYINT(1) DEFAULT 0                 NOT NULL,
    anonymous_name    VARCHAR(30)                          NULL,
    created_at        DATETIME   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_date      DATE                                 NOT NULL,
    updated_at        DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    is_deleted        TINYINT(1) DEFAULT 0                 NOT NULL,
    CONSTRAINT unique_channel_today_question UNIQUE (channel_uuid, created_date),
    CONSTRAINT questions_ibfk_1 FOREIGN KEY (channel_uuid) REFERENCES channels (uuid),
    CONSTRAINT questions_ibfk_2 FOREIGN KEY (channel_member_id) REFERENCES channel_members (id) ON DELETE SET NULL
);

create TABLE if not exists `pickitalki`.answers
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id       BIGINT                               NOT NULL,
    channel_member_id BIGINT                               NULL,
    content           VARCHAR(500)                         NOT NULL,
    is_anonymous      TINYINT(1) DEFAULT 0                 NOT NULL,
    anonymous_name    VARCHAR(10)                          NULL,
    created_at        DATETIME   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at        DATETIME   DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
    is_deleted        TINYINT(1) DEFAULT 0                 NOT NULL,
    CONSTRAINT answers_ibfk_1 FOREIGN KEY (question_id) REFERENCES questions (id) ON DELETE CASCADE,
    CONSTRAINT answers_ibfk_3 FOREIGN KEY (channel_member_id) REFERENCES channel_members (id) ON DELETE SET NULL
);

-- 1. 기존 인덱스 여부 확인 후 조건부 생성
SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'answers'
      AND index_name = 'idx_answers_question_id'
);

-- 2. 인덱스가 없으면 생성
SET @create_index = IF(@index_exists = 0, 'CREATE INDEX idx_answers_question_id ON `pickitalki`.`answers` (`question_id`);', 'SELECT ''Index exists'';');
PREPARE stmt FROM @create_index;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;





