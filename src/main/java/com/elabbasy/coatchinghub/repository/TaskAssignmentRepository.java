package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.TaskAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    List<TaskAssignment> findByCoacheeId(Long coacheeId);

    @Query("""
    SELECT ta
    FROM TaskAssignment ta
    JOIN FETCH ta.taskTemplate tt
    WHERE ta.coachee.id = :coacheeId
    ORDER BY ta.createdDate DESC
""")
    Page<TaskAssignment> findByCoacheeId(@Param("coacheeId") Long coacheeId, Pageable pageable);

    @Query("""
    SELECT ta
    FROM TaskAssignment ta
    JOIN FETCH ta.taskTemplate tt
    WHERE ta.booking.id = :bookingId
    ORDER BY ta.createdDate DESC
""")
    Page<TaskAssignment> findByBookingId(@Param("bookingId") Long bookingId, Pageable pageable);

    @Query("""
    SELECT ta
    FROM TaskAssignment ta
    JOIN FETCH ta.taskTemplate tt
    WHERE ta.id = :assignmentId
""")
    Optional<TaskAssignment> findDetailsBase(@Param("assignmentId") Long assignmentId);
}
