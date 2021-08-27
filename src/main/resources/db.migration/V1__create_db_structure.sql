create table images (
    id uuid not null,
    url varchar(255),
    hash bigint,
    primary key (id)
);