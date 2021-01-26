/*
 version=1.0.0
 remark=创建测试表
*/
create table dcc_demo
(
    id int not null
        constraint dcc_demo_pk
            primary key,
    name varchar(50)
);

alter table dcc_demo add column user_id varchar(50) not null default 'dist';

/*
version=1.0.0
remark=插入测试数据 dist
*/
insert into dcc_demo values(1,'dist','hello');

/*
version=2.0.1
remark=插入测试数据 xdata
*/
insert into dcc_demo values(2,'xdata','world');