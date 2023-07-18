create DATABASE `quarkus-social`;

create table USERS (
    id BIGINT AUTO_INCREMENT not null primary key,
    name varchar(100) not null,
    age integer not null
);

create table POSTS (
    id BIGINT AUTO_INCREMENT not null primary key,
    post_text varchar(150) not null,
    date_time timestamp not null,
    user_id BIGINT not null references USERS(id)
);