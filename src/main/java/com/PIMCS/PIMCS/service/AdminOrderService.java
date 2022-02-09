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
        System.out.println(matCategory.getMatCategoryName());
        matCategoryRepository.save(matCategory);
    }

    public void modifyMatCategory(MatCategory matCategory){
        Optional<MatCategory> matCategorySource=matCategoryRepository.findByMatCategoryName(matCategory.getMatCategoryName());

        if(matCategorySource.isPresent()){
            matCategory.setId(matCategorySource.get().getId());
            matCategoryRepository.save(matCategory);
        }

    }
    public List<MatCategory> findMatCategory(){
        return matCategoryRepository.findAll();
    }

    public void removeMatCategory(Integer id){
        Optional<MatCategory> matCategory=matCategoryRepository.findById(id);
        if(matCategory.isPresent()){
            matCategoryRepository.delete(matCategory.get());
        }
    }
}
