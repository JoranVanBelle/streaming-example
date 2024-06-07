create table if not exists "feedback"
(
    "feedback_id" VARCHAR(64) primary key,
    "timestamp" timestamp not null,
    "username" varchar(64) not null,
    "comment" varchar(64) not null,
    "location" varchar(64) not null
)