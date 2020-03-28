create table IDEAS (
    id bigserial primary key,
    author_id bigint references USERS(id),
    created bigint,
    content text,
    media text,
    link text,
    votes text
);
create table VOTES (
    id bigserial primary key,
    author_id bigint references USERS(id),
    idea_id bigint references IDEAS(id),
    created bigint,
    is_up boolean
)