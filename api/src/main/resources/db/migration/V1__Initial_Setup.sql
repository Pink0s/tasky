create table if not exists user_account (
    id bigserial primary key,
    first_name text not null,
    last_name text not null,
    email text unique not null,
    password text not null,
    never_connected bool default false NOT NULL,
    role text NOT NULL default 'USER' CHECK( role in ('USER','ADMIN','PROJECT_MANAGER')),
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists project (
    id bigserial primary key,
    name text not null,
    due_date timestamp not null,
    description text,
    created_by bigint references user_account(id) not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists user_account_project (
    user_account_id bigint references user_account(id) not null,
    project_id bigint references project(id) not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null,
    primary key(user_account_id,project_id)
);

create table if not exists run (
    id bigserial primary key,
    name text not null,
    description text,
    start_date timestamp not null,
    end_date timestamp not null,
    total_task integer default 0,
    completed_task integer default 0,
    project_id bigint references project(id) not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists feature (
  id bigserial primary key,
  name text not null,
  description text,
  type text not null,
  run_id bigint references run(id),
  project_id bigint references project(id),
  created_at timestamp default CURRENT_TIMESTAMP not null,
  updated_at timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists to_do(
    id bigserial primary key,
    name text not null,
    type text not null check( type in ('task','bug') ),
    description text,
    status text default 'New' not null check( status in ('New','In progress','Done') ) ,
    feature_id bigint references feature(id) not null,
    user_id bigint references user_account(id),
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists comment(
    id bigserial primary key,
    name text not null,
    content text not null,
    to_do_id bigint references to_do(id) not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);