CREATE TABLE USERS(
    id serial primary key,
    username varchar(100) unique,
    password varchar(100),
    likes int,
    not_likes int,
    is_hater boolean,
    is_promoter boolean
)