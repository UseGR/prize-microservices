create table users
(
    id              bigint primary key,
    firstname       varchar(255),
    username        varchar(255),
    fullname        varchar(255),
    phone_number    varchar(255),
    date_of_birth   varchar(255),
    sex             varchar(255),
    know_from       varchar(255),
    is_participated boolean,
    is_admin        boolean,
    is_blocked      boolean
);