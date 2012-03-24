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
 error bool,
 unique (name, revision));

create table warnings (
 level int, -- http://stackoverflow.com/questions/5007720/good-guidance-for-when-to-use-which-log-level-info-warn-debug-etc-for-appli
 -- 0: emergency, 1: alert, 2: critical, 3: error, 4: warning, 5: notice, 6: informational, 7: debug, 8: trace
 warning text,
 name varchar(50) not null,
 revision varchar(50) not null);
