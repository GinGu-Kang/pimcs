package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Answer;
import com.PIMCS.PIMCS.domain.MatCategory;
import com.PIMCS.PIMCS.domain.Question;
import com.PIMCS.PIMCS.repository.AnswerRepository;
import com.PIMCS.PIMCS.repository.MatCategoryRepository;
import com.PIMCS.PIMCS.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminOrderService {
    @Autowired
    private final AnswerRepository answerRepository;
    @Autowired
    private final MatCategoryRepository matCategoryRepository;
    @Autowired
    private final QuestionRepository questionRepository;

    @Autowired
    public AdminOrderService(AnswerRepository answerRepository, MatCategoryRepository matCategoryRepository, QuestionRepository questionRepository) {
        this.answerRepository = answerRepository;
        this.matCategoryRepository = matCategoryRepository;
        this.questionRepository = questionRepository;
    }


    public void addMatCategory(MatCategory matCategory){
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

    public List<MatCategory> findMatCategoryList(List<Integer> matCategoryIdList){
        return matCategoryRepository.findAllById(matCategoryIdList);
    }

    //댓글 추가 getOne 방식을 사용하면 select + insert 가 아닌 insert 쿼리만 나간다.
    public void addAnswer(Integer questionId, Answer answer){
        Question question=questionRepository.getOne(questionId);

        answer.setQuestion(question);

        answerRepository.save(answer);
    }



}
