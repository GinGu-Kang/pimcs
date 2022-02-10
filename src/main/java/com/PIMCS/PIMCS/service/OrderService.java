package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.MatCategoryOrder;
import com.PIMCS.PIMCS.domain.MatOrder;
import com.PIMCS.PIMCS.form.MatCategoryAndOrderList;
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

    public void saveOrder(MatOrder matOrder, SecUserCustomForm user, List<MatCategoryOrder> matCategoryAndOrderList,List<Integer> matCategoryId){
        List<MatCategory> matCategoryList=matCategoryRepository.findAllById(matCategoryId);

        matOrder.setCompany(user.getCompany());
        matOrder.setUser(userRepository.findByEmail(user.getUsername()).get());
        matOrderRepository.save(matOrder);

        for(int i=0;i<matCategoryAndOrderList.size();i++){
            matCategoryAndOrderList.get(i).setMatOrder(matOrder);
            matCategoryAndOrderList.get(i).setMatCategory(matCategoryList.get(i));
        }

        matCategoryOrderRepository.saveAll(matCategoryAndOrderList);
    }




}
