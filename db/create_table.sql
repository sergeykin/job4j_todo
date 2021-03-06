drop table item;
create table item(
                     id SERIAL PRIMARY KEY,
                     "desc" TEXT,
                     created timestamp,
                     done boolean,
                     user_id int not null references j_user(id)
);

create table j_role (
                        id serial primary key,
                        name varchar(2000)
);

drop table  j_user cascade;
create table j_user (
                          id serial primary key,
                          name varchar(2000)
    --,role_id int not null references j_role(id)
  );

create table car_model (
                        id serial primary key,
                        name varchar(2000)
);

create table car_brand (
                        id serial primary key,
                        name varchar(2000)
);


create table engine (
    id serial primary key
);

create table driver (
    id serial primary key
);

create table car (
                     id serial primary key,
                     engine_id int not null unique references engine(id)
);

create table history_owner (
                               id serial primary key,
                               driver_id int not null references driver(id),
                               car_id int not null references car(id)
);