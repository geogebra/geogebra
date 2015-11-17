create table engines (
 name text not null primary key,
 info text);

insert into engines (name, info) values ('GG', 'GeoGebra''s internal prover');
insert into engines (name, info) values ('OGP', 'OpenGeoProver');

create table methods (
 name text not null primary key,
 info text);

insert into methods (name, info) values ('Recio', 'Recio''s exact check method');
insert into methods (name, info) values ('Botana', 'The Gröbner basis method');
insert into methods (name, info) values ('Wu', 'Wu''s characteristic set method');
insert into methods (name, info) values ('Area', 'The area method');
insert into methods (name, info) values ('Auto', 'Default prover');

create table platforms (
 name text not null primary key,
 info text);

insert into platforms (name, info) values ('desktop', 'Java native');
insert into platforms (name, info) values ('web', 'JavaScript');

create table outsourcings (
 name text,
 info text);

insert into outsourcings (name, info) values ('', 'no outsourcing: using internals');
insert into outsourcings (name, info) values ('SingularWS', 'Singular WebService');

create table provers (
 name text not null primary key,
 engine text,
 method text,
 outsourcing text,
 platform text,
 ndg boolean
);

insert into provers (name, engine, method, outsourcing, platform, ndg) values ('Recio', 'GG', 'Recio', '', 'desktop', 0);
insert into provers (name, engine, method, outsourcing, platform, ndg) values ('BotanaGiac', 'GG', 'Botana', '', 'desktop', 1);
insert into provers (name, engine, method, outsourcing, platform, ndg) values ('Botana', 'GG', 'Botana', 'SingularWS', 'desktop', 1);
insert into provers (name, engine, method, outsourcing, platform, ndg) values ('Auto', 'GG', 'Auto', '', 'desktop', 1);
insert into provers (name, engine, method, outsourcing, platform, ndg) values ('OpenGeoProver_Wu', 'OGP', 'Wu', '', 'desktop', 1);
insert into provers (name, engine, method, outsourcing, platform, ndg) values ('OpenGeoProver_Area', 'OGP', 'Area', '', 'desktop', 0);
insert into provers (name, engine, method, outsourcing, platform, ndg) values ('Web', 'GG', 'Auto', '', 'web', 1);

-- Using Jenkins' environment settings, assuming SVN being used,
-- see https://wiki.jenkins-ci.org/display/JENKINS/Building+a+software+project
create table builds (
 build_number int not null primary key,
 svn_revision int,
 build_url text,
 machine text);

create table testcases (
 name text not null primary key);

create table tests (
 testcase text, -- references testcases.name
 prover text, -- references provers.name
 ndg boolean,
 build_number int not null, -- references builds.build_number
 start_unixtime int, -- when did the test start
 osresult int, -- exit code from OS (Linux)
 timeout_setting int,
 accepted bool,
 errortype int, -- 1: runtime error, 2: result error, 3: timeout
 speed int, -- in milliseconds
 regressionfile text,
 logfile text,
 stdout text,
 stderr text,
 unique (testcase, prover, ndg, build_number));
