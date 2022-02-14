package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Company;
import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.MatCategoryOrder;
import com.PIMCS.PIMCS.domain.MatOrder;
import com.PIMCS.PIMCS.form.MatCategoryAndOrderForm;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.repository.MatCategoryOrderRepository;
import com.PIMCS.PIMCS.repository.MatCategoryRepository;
import com.PIMCS.PIMCS.repository.MatOrderRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final MatCategoryRepository matCategoryRepository;
    private final MatCategoryOrderRepository matCategoryOrderRepository;
    private final MatOrderRepository matOrderRepository;
    private final UserRepository userRepository;


    public OrderService(MatCategoryRepository matCategoryRepository, MatCategoryOrderRepository matCategoryOrderRepository, MatOrderRepository matOrderRepository, UserRepository userRepository) {
        this.matCategoryRepository = matCategoryRepository;
        this.matCategoryOrderRepository = matCategoryOrderRepository;
        this.matOrderRepository = matOrderRepository;
        this.userRepository = userRepository;
    }


    public void saveOrder(MatOrder matOrder, SecUserCustomForm user, MatCategoryAndOrderForm matCategoryAndOrderForm){
        List<MatCategoryOrder> matCategoryOrderList= matCategoryAndOrderForm.getMatCategoryOrderList();
        List<Integer> matCategoryId= matCategoryAndOrderForm.getMatCategoryIdList();
        List<MatCategory> matCategoryList=matCategoryRepository.findAllById(matCategoryId);

        matOrder.setCompany(user.getCompany());
        matOrder.setUser(userRepository.findByEmail(user.getUsername()).get());
        matOrderRepository.save(matOrder);

        for(int i=0;i<matCategoryOrderList.size();i++){
            matCategoryOrderList.get(i).setMatOrder(matOrder);
            matCategoryOrderList.get(i).setMatCategory(matCategoryList.get(i));
        }

        matCategoryOrderRepository.saveAll(matCategoryOrderList);
    }

    public List<MatOrder> findOrder(Company company){
        return matOrderRepository.findByCompany(company);
    }

    /**
     * 회사별 주문 조회
     * @param companyId
     * @return 매트 카테고리, 카테고리 별 주문갯수, 주문자 정보
     */
    public List<MatCategoryOrder> findOrderList(Integer companyId){
        return matCategoryOrderRepository.findByCompanyId(companyId);
    }

}
