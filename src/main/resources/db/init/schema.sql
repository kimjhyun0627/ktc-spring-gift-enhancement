CREATE TABLE PRODUCT
(
    ID        BIGINT AUTO_INCREMENT PRIMARY KEY,
    NAME      VARCHAR(15)  NOT NULL,
    PRICE     INT          NOT NULL,
    IMAGE_URL VARCHAR(255) NOT NULL,
    HIDDEN    BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE MEMBER
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(64)  NOT NULL,
    role          VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE WISH
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    amount     INT    NOT NULL,

    CONSTRAINT fk_wish_member
        FOREIGN KEY (member_id)
            REFERENCES MEMBER (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_wish_product
        FOREIGN KEY (product_id)
            REFERENCES PRODUCT (id)
            ON DELETE RESTRICT
);
