select * from matOrder INNER JOIN  company on matOrder.companyId =company.id
    INNER JOIN matCategoryOrder on matOrder.id = matCategoryOrder.orderId
    INNER JOIN matCategory on matCategory.id = matCategoryOrder.matCategoryId
    where company.id=2;

select * from matCategoryOrder INNER JOIN  matOrder on matCategoryOrder.orderId = matOrder.id
                       INNER JOIN company on matOrder.companyId = company.id
                       INNER JOIN matCategory on matCategory.id = matCategoryOrder.matCategoryId
where company.id=1;