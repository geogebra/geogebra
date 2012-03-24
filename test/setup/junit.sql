create table revisions (
 id varchar(50) not null primary key,
 tested timestamp not null);

create table names (
 id varchar(50) not null primary key);

create table tests (
 classname varchar(50),
 name varchar(50) not null,
 message text,
 type varchar(100),
 revision varchar(50) not null,
 unique (name, revision));

create table warnings (
 level int,
 warning text,
 name varchar(50) not null,
 revision varchar(50) not null);
