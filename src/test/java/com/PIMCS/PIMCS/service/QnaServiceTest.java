package com.PIMCS.PIMCS.service;

import com.PIMCS.PIMCS.domain.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class QnaServiceTest {
    @Autowired
    QnaService qnaService;

    @Test
    void filterQuestion() {
        Pageable pageable= PageRequest.of(0, 5, Sort.Direction.DESC, "createdAt");
        Page<Question> qeustions=qnaService.filterQuestion("test","제목",pageable);
        assertEquals("test",qeustions.getContent().get(0).getTitle());
    }
}