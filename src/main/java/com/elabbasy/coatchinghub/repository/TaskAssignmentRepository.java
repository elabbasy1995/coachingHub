package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.TaskAssignment;
import com.elabbasy.coatchinghub.model.enums.TaskAssignmentStatus;
import com.elabbasy.coatchinghub.model.response.PortalDashboardTaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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

    @Query(value = """
    SELECT new com.elabbasy.coatchinghub.model.response.PortalDashboardTaskResponse(
        ta.id,
        tt.title,
        ta.dueDate,
        CASE WHEN ta.status = :completedStatus THEN true ELSE false END,
        coach.fullNameEn,
        coach.fullNameAr,
        coachee.fullName,
        coachee.phoneNumber
    )
    FROM TaskAssignment ta
    JOIN ta.taskTemplate tt
    JOIN tt.coach coach
    JOIN ta.coachee coachee
    WHERE ta.dueDate >= :startDate
      AND ta.dueDate <= :endDate
    ORDER BY ta.dueDate ASC, ta.createdDate DESC
""",
            countQuery = """
    SELECT COUNT(ta)
    FROM TaskAssignment ta
    WHERE ta.dueDate >= :startDate
      AND ta.dueDate <= :endDate
""")
    Page<PortalDashboardTaskResponse> findDashboardTasksBetweenDueDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("completedStatus") TaskAssignmentStatus completedStatus,
            Pageable pageable
    );
}
