create table engines (
 name text not null primary key,
 info text);

insert into engines (name, info) values ('GG', 'GeoGebra''s internal prover');

create table methods (
 name text not null primary key,
 info text);

insert into methods (name, info) values ('default', 'Default prover');
insert into methods (name, info) values ('noelim', 'Use RealGeomWS only, do not use elimination');


create table platforms (
 name text not null primary key,
 info text);

insert into platforms (name, info) values ('desktop', 'Java native');
insert into platforms (name, info) values ('web', 'JavaScript');

create table provers (
 name text not null primary key,
 engine text,
 method text,
 platform text
);

insert into provers (name, engine, method, platform) values ('Classic5', 'GG', 'default', 'desktop');
insert into provers (name, engine, method, platform) values ('Classic5-rg', 'GG', 'noelim', 'desktop');
insert into provers (name, engine, method, platform) values ('Classic6', 'GG', 'default', 'web');

-- Using Jenkins' environment settings, assuming Git being used,
-- see https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project
create table builds (
 build_number int not null primary key,
 git_revision int,
 build_url text,
 machine text);

create table testcases (
 name text not null primary key);

create table tests (
 testcase text, -- references testcases.name
 prover text, -- references provers.name
 build_number int not null, -- references builds.build_number
 start_unixtime int, -- when did the test start
 result text, -- textual result
 osresult int, -- exit code from OS (Linux)
 timeout_setting int,
 accepted bool,
 errortype int, -- 1: runtime error, 2: result error, 3: timeout
 speed int, -- in milliseconds
 regressionfile text,
 logfile text,
 stdout text,
 stderr text,
 unique (testcase, prover, build_number));
