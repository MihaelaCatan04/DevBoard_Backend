CREATE TABLE IF NOT EXISTS posts
(
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    body       TEXT         NOT NULL,
    author     VARCHAR(100) NOT NULL,
    tag        VARCHAR(50),
    votes      INT       DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comments
(
    id         BIGSERIAL PRIMARY KEY,
    post_id    BIGINT REFERENCES posts (id) ON DELETE CASCADE,
    author     VARCHAR(100) NOT NULL,
    body       TEXT         NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users
(
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(255)        NOT NULL,
    role       VARCHAR(20) DEFAULT 'WRITER',
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE posts
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP DEFAULT NULL;

CREATE TABLE IF NOT EXISTS post_votes
(
    username   VARCHAR(100) NOT NULL,
    post_id    BIGINT       NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
    direction  INT          NOT NULL CHECK (direction IN (-1, 1)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (username, post_id)
);

