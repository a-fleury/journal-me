CREATE TABLE sessions (
  id UUID PRIMARY KEY,
  token VARCHAR(255) NOT NULL,
  starts_at TIMESTAMP WITH TIME ZONE NOT NULL,
  ends_at TIMESTAMP WITH TIME ZONE NOT NULL,
  user_id UUID NOT NULL,

  CONSTRAINT sessions_user_fk
      FOREIGN KEY (user_id)
          REFERENCES users(id)
          ON DELETE CASCADE,

  CONSTRAINT sessions_token_unique
      UNIQUE (token)
);
