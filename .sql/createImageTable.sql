CREATE TABLE images(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    path VARCHAR(255) not null,
    article_id BIGINT,
    FOREIGN KEY (article_id) REFERENCES articles(id)
);
