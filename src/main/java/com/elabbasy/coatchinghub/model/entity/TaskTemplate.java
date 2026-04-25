package com.elabbasy.coatchinghub.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task_templates")
@SQLRestriction("DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} task_templates SET DELETED = true WHERE id = ?")
public class TaskTemplate extends AuditBaseEntity {

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;

    @OrderBy("orderIndex ASC")
    @OneToMany(mappedBy = "taskTemplate")
    private Set<TaskQuestion> questions;
}