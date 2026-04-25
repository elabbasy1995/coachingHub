package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.TaskAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAnswerRepository extends JpaRepository<TaskAnswer, Long> {

    List<TaskAnswer> findByAssignmentId(Long assignmentId);
}
