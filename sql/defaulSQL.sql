use pimcs;
alter table matCategory CHANGE matInfomation matInformation blob;
use pimcs;
TRUNCATE matOrder; #테이블 데이터 전체삭제
DELETE FROM matOrder where orderCnt=null;

alter table matOrder add pgOrderId varchar (60) not null;