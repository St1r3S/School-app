
drop table if exists groups;

create table groups
(
    group_id   int,
    group_name text unique
);
