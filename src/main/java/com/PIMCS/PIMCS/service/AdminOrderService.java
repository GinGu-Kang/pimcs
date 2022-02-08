package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.repository.MatCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminOrderService {

    private final MatCategoryRepository matCategoryRepository;

    @Autowired
    public AdminOrderService(MatCategoryRepository matCategoryRepository) {
        this.matCategoryRepository = matCategoryRepository;
    }


    public void addMatCategory(MatCategory matCategory){
        System.out.println(matCategory.getMatCategory());
        matCategoryRepository.save(matCategory);
    }

    public void modifyMatCategory(MatCategory matCategory){
        if(matCategoryRepository.findById(matCategory.getId()).isPresent()){
            matCategoryRepository.save(matCategory);
        }

    }
    public List<MatCategory> findMatCategory(){
        return matCategoryRepository.findAll();
    }

    public void removeMatCategory(String matCategoryName){
        Optional<MatCategory> matCategory=matCategoryRepository.findByMatCategory(matCategoryName);
        if(matCategory.isPresent()){
            matCategoryRepository.delete(matCategory.get());
        }
    }
}
