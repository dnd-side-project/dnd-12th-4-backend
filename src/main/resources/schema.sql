

CREATE TABLE if not exists `pickitalki`.members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(30) NOT NULL
);

CREATE TABLE if not exists `pickitalki`.channels (
    uuid BINARY(16) PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE if not exists `pickitalki`.channel_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_uuid BINARY(16) NOT NULL,
    member_id BIGINT NOT NULL,
    FOREIGN KEY (channel_uuid) REFERENCES channels(uuid) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);

CREATE TABLE if not exists `pickitalki`.questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_id BINARY(16) NOT NULL,
    author_id BIGINT NOT NULL,  -- 작성자 정보는 항상 저장됨
    content VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    anonymous_name VARCHAR(30),  -- 익명 닉네임 저장 가능
    FOREIGN KEY (channel_id) REFERENCES channels(uuid) ON DELETE CASCADE,
    FOREIGN KEY (author_id) REFERENCES members(id) ON DELETE CASCADE
);

CREATE TABLE if not exists `pickitalki`.today_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_id BIGINT NOT NULL UNIQUE,
    question_id BIGINT NULL, -- 질문이 없을 수도 있음
    created_date DATE NOT NULL,
    FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE SET NULL,
    UNIQUE KEY unique_channel_today_question (channel_id, created_date)
);

CREATE TABLE if not exists `pickitalki`.answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    content VARCHAR(500) NOT NULL,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    anonymous_name VARCHAR(10),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE
);