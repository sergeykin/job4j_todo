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

create table j_user (
                        id serial primary key,
                        name varchar(2000),
                        role_id int not null references j_role(id)
);