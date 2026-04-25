package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.TaskQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskQuestionRepository extends JpaRepository<TaskQuestion, Long> {
    List<TaskQuestion> findByTaskTemplateIdOrderByOrderIndex(Long templateId);

    @Query("""
    SELECT q
    FROM TaskQuestion q
    WHERE q.taskTemplate.id = :templateId
    ORDER BY q.orderIndex
""")
    List<TaskQuestion> findByTemplateId(@Param("templateId") Long templateId);
}