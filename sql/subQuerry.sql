select distinct c.*
from company c ,ownDevice o
where c.id = o.companyId and o.serialNumber = '125';



select *
from company
where id in (select companyId from ownDevice where serialNumber='125');

