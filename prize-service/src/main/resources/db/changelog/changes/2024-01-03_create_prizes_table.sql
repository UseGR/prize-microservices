CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";

create table prizes
(
    id                 UUID default (uuid_generate_v4()) primary key,
    prize_description  varchar(255),
    winner_description varchar(255),
    file_id            varchar(255),
    is_animation       boolean,
    is_rolled          boolean,
    user_id            bigint
);
