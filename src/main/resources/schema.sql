

CREATE TABLE if not exists `pickitalki`.members (
    id VARCHAR(30) NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE if not exists `pickitalki`.channels (
    uuid BINARY(16) PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE if not exists `pickitalki`.channel_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_uuid BINARY(16) NOT NULL,
    member_id VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (channel_uuid) REFERENCES channels(uuid),
    FOREIGN KEY (member_id) REFERENCES members(id)
);

CREATE TABLE if not exists `pickitalki`.questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_id BINARY(16) NOT NULL,
    author_id VARCHAR(30) NOT NULL,
    content VARCHAR(255) NOT NULL,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    anonymous_name VARCHAR(30),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (channel_id) REFERENCES channels(uuid),
    FOREIGN KEY (author_id) REFERENCES members(id)
);

CREATE TABLE if not exists `pickitalki`.answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    content VARCHAR(500) NOT NULL,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    anonymous_name VARCHAR(10),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (question_id) REFERENCES questions(id),
    FOREIGN KEY (member_id) REFERENCES members(id)
);