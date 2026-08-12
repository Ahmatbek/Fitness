CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 50;
CREATE sequence trainees_seq START WITH 1 INCREMENT BY 50;
create sequence trainers_seq start with 1 increment by 50;
create sequence training_type_seq start with 1 increment by 50;
create sequence training_seq start with 1 increment by 50;


create table users
(
    id         bigint primary key,
    is_active  boolean      not null default true,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    password   varchar      not null,
    username varchar(255) not null unique
);

create table training_types
(
    id   bigint primary key,
    name varchar not null
);

create table trainees
(
    id            bigint primary key,
    address       varchar(255),
    date_of_birth DATE,
    user_id       bigint not null unique ,
    constraint trainees_users
        foreign key (user_id)
            references users (id) on delete cascade
);

create table trainers
(
    id bigint primary key,
    user_id           BIGINT NOT NULL UNIQUE,
    specialization_id      BIGINT NOT NULL,
    constraint trainers_users
        foreign key (user_id)
            references users (id)  on delete cascade ,
    constraint trainers_training_types
        foreign key (specialization_id)
            references training_types (id)  on delete cascade
);

create table trainings
(
    id            bigint primary key,
    date          date,
    duration      integer,
    trainee_id    bigint not null ,
    trainer_id    bigint not null ,
    training_type_id bigint not null,
    constraint training_trainee foreign key (trainee_id) references trainees (id)  on delete cascade ,
    constraint training_trainer foreign key (trainer_id) references trainers (id)  on delete cascade ,
    constraint training_type foreign key (training_type_id) references training_types (id)  on delete cascade ,
    training_name varchar(255)
);

create table trainee_traineer(
                                 trainee_id bigint not null,
                                 trainer_id bigint not null,
                                 PRIMARY KEY (trainee_id, trainer_id),
    constraint new_trainee_constraint foreign key (trainee_id) references trainees(id)  on delete cascade ,
    constraint new_trainer_constraint foreign key (trainer_id) references trainers(id)  on delete cascade
)