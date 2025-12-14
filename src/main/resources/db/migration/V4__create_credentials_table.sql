CREATE TABLE credentials (
 user_id UUID PRIMARY KEY,
 password_hash TEXT NOT NULL,

 CONSTRAINT credentials_user_fk
     FOREIGN KEY (user_id)
         REFERENCES users(id)
         ON DELETE CASCADE
);
