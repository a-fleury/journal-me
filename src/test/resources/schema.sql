CREATE TABLE users (
   id UUID PRIMARY KEY,
   first_name VARCHAR(100) NOT NULL,
   last_name  VARCHAR(100) NOT NULL,
   email      VARCHAR(255) NOT NULL,
   genre      VARCHAR(50)  NOT NULL,

   CONSTRAINT users_email_unique UNIQUE (email)
);

CREATE TABLE journal_events (
    id UUID PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    date TIMESTAMP WITH TIME ZONE NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    ended_at TIMESTAMP WITH TIME ZONE NOT NULL,
    user_id UUID NOT NULL,

    CONSTRAINT journal_event_user_fk
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE
);

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

CREATE TABLE credentials (
    user_id UUID PRIMARY KEY,
    password_hash TEXT NOT NULL,
    
    CONSTRAINT credentials_user_fk
     FOREIGN KEY (user_id)
         REFERENCES users(id)
         ON DELETE CASCADE
);



