CREATE DATABASE calendar;
USE calendar;
create table events (eid int(10) auto_increment, edate DATE, edesc varchar(256), primary key (eid));