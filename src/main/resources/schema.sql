
create TABLE if not exists `pickitalki`.members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    kakao_id BIGINT UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE,
    nick_name VARCHAR(50) NOT NULL,
    profile_image_url TEXT NULL,
    refresh_token TEXT NULL,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP ON update CURRENT_TIMESTAMP,
    boolean is_deleted DEFAULT FALSE
);

create TABLE if not exists `pickitalki`.channels (
    uuid BINARY(16) PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    boolean is_deleted DEFAULT FALSE
);

create TABLE if not exists `pickitalki`.channel_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_uuid BINARY(16) NOT NULL,
    member_id VARCHAR(30) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    boolean is_deleted DEFAULT FALSE,
    FOREIGN KEY (channel_uuid) REFERENCES channels(uuid),
    FOREIGN KEY (member_id) REFERENCES members(id)
);

create TABLE if not exists `pickitalki`.questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    channel_uuid BINARY(16) NOT NULL,
    author_id VARCHAR(30) NOT NULL,
    content VARCHAR(255) NOT NULL,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    anonymous_name VARCHAR(30),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    boolean is_deleted DEFAULT FALSE,
    FOREIGN KEY (channel_uuid) REFERENCES channels(uuid),
    FOREIGN KEY (author_id) REFERENCES members(id)
);

create TABLE if not exists `pickitalki`.answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    content VARCHAR(500) NOT NULL,
    is_anonymous BOOLEAN NOT NULL DEFAULT FALSE,
    anonymous_name VARCHAR(10),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    boolean is_deleted DEFAULT FALSE,
    FOREIGN KEY (question_id) REFERENCES questions(id),
    FOREIGN KEY (member_id) REFERENCES members(id)
);