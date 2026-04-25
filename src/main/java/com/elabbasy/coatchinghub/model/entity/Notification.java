package com.elabbasy.coatchinghub.model.entity;

import com.elabbasy.coatchinghub.model.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
@SQLRestriction("DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} notifications SET DELETED = true WHERE id = ?")
public class Notification extends AuditBaseEntity {

    private String title;
    private String body;

    @Column(nullable = false)
    private Boolean read = false;

    private LocalDateTime readAt;

    private Long referenceId;
    @Enumerated(EnumType.STRING)
    @Column
    private NotificationType notificationType;

    @ManyToOne
    @JoinColumn(name = "coach_id")
    private Coach coach;

    @ManyToOne
    @JoinColumn(name = "coachee_id")
    private Coachee coachee;
}