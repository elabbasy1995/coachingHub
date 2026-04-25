package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.CoachSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface CoachSlotRepository extends JpaRepository<CoachSlot, Long> {

    @Query("""
        select count(s) > 0
        from CoachSlot s
        where s.coach.id = :coachId
          and s.status <> 'CANCELLED'
          and s.startTimeUtc < :newEnd
          and s.endTimeUtc > :newStart
    """)
    boolean existsOverlappingSlot(
            @Param("coachId") Long coachId,
            @Param("newStart") OffsetDateTime newStart,
            @Param("newEnd") OffsetDateTime newEnd
    );

    @Query("SELECT s FROM CoachSlot s WHERE s.coach.id = :coachId")
    List<CoachSlot> findSlotsByCoach(@Param("coachId") Long coachId);

    @Query("SELECT s FROM CoachSlot s WHERE s.coach.id = :coachId and status = 'AVAILABLE'")
    List<CoachSlot> findAvailableSlotsByCoach(@Param("coachId") Long coachId);

    @Query("""
    SELECT cs
    FROM CoachSlot cs
    WHERE cs.coach.id = :coachId
      AND cs.startTimeUtc >= :start
      AND cs.startTimeUtc < :end
""")
    List<CoachSlot> findSlotsByCoachAndMonth(
            @Param("coachId") Long coachId,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end
    );

    @Query("""
    SELECT cs
    FROM CoachSlot cs
    WHERE cs.coach.id = :coachId
      AND cs.startTimeUtc >= :start
      AND cs.startTimeUtc < :end
      and status = 'AVAILABLE'
""")
    List<CoachSlot> findAvailableSlotsByCoachAndMonth(
            @Param("coachId") Long coachId,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end
    );


}
