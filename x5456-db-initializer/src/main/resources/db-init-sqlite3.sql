/*
version=0.0.0
remark=Init Version Table
*/
create table if not exists db_script
(
    id varchar(50)
    constraint dist_db_script_pk
    primary key,
    version varchar(50) not null,
    script text not null,
    create_time timestamp,
    remark varchar(500),
    app varchar(50)
);