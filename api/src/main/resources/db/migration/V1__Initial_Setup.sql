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
    status text not null default 'New' CHECK ( project.status in ('New', 'In progress', 'Completed') ),
    created_by bigint references user_account(id) on delete cascade not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists user_account_project (
    user_account_id bigint references user_account(id) on delete cascade not null,
    project_id bigint references project(id) on delete cascade not null,
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
    status text not null default 'New' CHECK ( run.status in ('New', 'In progress', 'Completed') ),
    project_id bigint references project(id) on delete cascade not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists feature (
    id bigserial primary key,
    name text not null,
    description text,
    run_id bigint references run(id) on delete cascade,
    project_id bigint references project(id) on delete cascade,
    status text not null default 'New' CHECK ( feature.status in ('New', 'In progress', 'Completed') ),
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists to_do(
    id bigserial primary key,
    name text not null,
    type text not null check( type in ('task','bug') ),
    description text,
    status text default 'New' not null check( status in ('New','In progress','Completed') ) ,
    feature_id bigint references feature(id) on delete cascade not null,
    user_id bigint references user_account(id) on delete cascade,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);

create table if not exists comment(
    id bigserial primary key,
    name text not null,
    content text not null,
    to_do_id bigint references to_do(id) on delete cascade not null,
    created_at timestamp default CURRENT_TIMESTAMP not null,
    updated_at timestamp default CURRENT_TIMESTAMP not null
);