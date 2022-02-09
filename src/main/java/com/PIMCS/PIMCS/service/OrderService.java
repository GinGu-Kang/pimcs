package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.MatOrder;
import com.PIMCS.PIMCS.form.SecUserCustomForm;
import com.PIMCS.PIMCS.repository.MatCategoryOrderRepository;
import com.PIMCS.PIMCS.repository.MatCategoryRepository;
import com.PIMCS.PIMCS.repository.MatOrderRepository;
import com.PIMCS.PIMCS.repository.UserRepository;
import org.springframework.stereotype.Service;

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

    public void saveOrder(MatOrder matOrder, String categoryCount, String categoryName,SecUserCustomForm user){
        String[] categoryCountList=categoryCount.split(",");
        String[] categoryNameList=categoryName.split(",");
        matOrder.setCompany(user.getCompany());
        matOrder.setUser(userRepository.findByEmail(user.getUsername()).get());
        matOrderRepository.save(matOrder);


    }




}
