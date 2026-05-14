CREATE TABLE tb_project (
    id         BIGSERIAL    PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    name       VARCHAR(120) NOT NULL,
    location   VARCHAR(500) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at TIMESTAMP    NOT NULL DEFAULT now(),
    CONSTRAINT uk_project_user_location UNIQUE (user_id, location)
);

