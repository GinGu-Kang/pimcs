select * from productCategory;
select * from user;
select * from role;
select * from userRole;
select * from product;
show tables;
desc matOrder;
select * from mat order by id desc;
desc mat;
select * from company;
alter table mat add currentInventory int not null;

alter table mat drop currentInventory;
alter table mat add currentInventory int not null default 0;
select * from mat;
select * from mat where threshold > currentInventory;


alter table company add `ceoName` VARCHAR(60) NULL;

select count(*) from mat order by id desc;

select * from user;

select * from userRole;

select * from mat where id=1331;

select * from mat;
select * from product order by id desc;

delete from product where id=2;


select * from mat;   

desc mat;

select * from mat where companyId=18;
select * from product where productCode='code123';
select * from company where id=18;
update company set ceoName="전룡호" where id=18;

desc product;
