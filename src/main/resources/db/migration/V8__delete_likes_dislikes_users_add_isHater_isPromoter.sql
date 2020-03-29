alter table USERS drop column likes;
alter table USERS drop column dislikes;
alter table USERS add column is_hater bool;
alter table USERS add column is_promoter bool;