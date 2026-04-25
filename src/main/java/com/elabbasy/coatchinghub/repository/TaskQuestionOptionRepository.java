package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.TaskQuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskQuestionOptionRepository extends JpaRepository<TaskQuestionOption, Long> {

    @Query("""
    SELECT o
    FROM TaskQuestionOption o
    WHERE o.question.id IN :questionIds
""")
    List<TaskQuestionOption> findByQuestionIdIn(@Param("questionIds") List<Long> questionIds);
}
