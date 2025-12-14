CREATE TABLE users (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    genre      VARCHAR(50)  NOT NULL,

    CONSTRAINT users_email_unique UNIQUE (email)
);
