package com.elabbasy.coatchinghub.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "task_question_options")
@SQLRestriction("DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} task_question_options SET DELETED = true WHERE id = ?")
public class TaskQuestionOption extends AuditBaseEntity {

    @Column(nullable = false)
    private String optionText;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private TaskQuestion question;
}