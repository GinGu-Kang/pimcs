use pimcs;
alter table pimcs.user modify column password varchar(100) default 0 after email;
select email,password from user where email = "rkdwlsrn212@gmail.com";

update user set enabled=1 where email='rkdwlsrn212@gmail.com';