package com.dnd12th_4.pickitalki.domain.answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer,Long> {

    Page<Answer> findByQuestionIdAndIsDeletedFalse(Long questionId, Pageable pageable);
}
