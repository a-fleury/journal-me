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
