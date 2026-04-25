package com.elabbasy.coatchinghub.model.entity;

import com.elabbasy.coatchinghub.model.enums.SlotStatus;
import com.elabbasy.coatchinghub.model.enums.SlotType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "coach_slots")
@SQLRestriction("DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} coach_slots SET DELETED = true where id = ?")
public class CoachSlot extends AuditBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coach_id", nullable = false)
    @JsonIgnoreProperties("user")
    private Coach coach;

    /**
     * Start of the slot in UTC
     * Example: 2026-01-20T14:00:00Z
     */
    @Column(name = "start_time_utc", nullable = false)
    private OffsetDateTime startTimeUtc;

    /**
     * End of the slot in UTC
     * Derived from start + period, but stored for fast queries.
     */
    @Column(name = "end_time_utc", nullable = false)
    private OffsetDateTime endTimeUtc;

    /**
     * Slot duration in minutes (e.g. 30, 45, 60)
     */
    @Column(name = "period_minutes", nullable = false)
    private Integer periodMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SlotType slotType;
}
